package com.example.rule.engine;

import com.alibaba.fastjson.JSON;
import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;
import com.example.rule.model.RuleExecutionLog;
import com.example.rule.model.RuleStatus;
import com.example.rule.service.RuleExecutionLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于MVEL的规则引擎实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MVELRuleEngine implements RuleEngine {
    private final RuleExecutionLogService logService;

    /**
     * 规则条件编译缓存
     */
    private final ConcurrentMap<Long, Object> conditionCache = new ConcurrentHashMap<>();

    /**
     * 规则动作编译缓存
     */
    private final ConcurrentMap<Long, Object> actionCache = new ConcurrentHashMap<>();

    @Override
    public Object execute(Rule rule, RuleContext context) {
        long startTime = System.currentTimeMillis();
        RuleExecutionLog log = new RuleExecutionLog()
                .setRuleId(rule.getId())
                .setRuleCode(rule.getCode())
                .setInput(JSON.toJSONString(context.getParams()));

        try {
            // 检查规则状态
            if (rule.getStatus() != RuleStatus.ENABLED) {
                log.setStatus(RuleExecutionLog.ExecutionStatus.FAILED)
                    .setErrorMessage("规则已禁用");
                log.setExecutionTime(System.currentTimeMillis() - startTime);
                logService.logExecution(log);
                return null;
            }

            // 编译并执行条件
            Object compiledCondition = getCompiledCondition(rule);
            boolean matches = MVEL.executeExpression(compiledCondition, context.getParams(), Boolean.class);

            if (!matches) {

                log.setStatus(RuleExecutionLog.ExecutionStatus.SUCCESS)
                    .setOutput("条件不匹配");
                log.setExecutionTime(System.currentTimeMillis() - startTime);
                logService.logExecution(log);
                return null;
            }

            // 编译并执行动作
            Object compiledAction = getCompiledAction(rule);
            Object result = MVEL.executeExpression(compiledAction, context.getParams());
            context.setResult(result);

            // 记录成功日志
            log.setStatus(RuleExecutionLog.ExecutionStatus.SUCCESS)
                .setOutput(JSON.toJSONString(result));
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            logService.logExecution(log);
            
            return result;
        } catch (Exception e) {
            // 记录失败日志
            log.setStatus(RuleExecutionLog.ExecutionStatus.FAILED)
                .setErrorMessage(e.getMessage());
            log.setExecutionTime(System.currentTimeMillis() - startTime);
            logService.logExecution(log);
            
            throw new RuntimeException("规则执行异常: " + rule.getCode(), e);
        }
    }

    @Override
    public Object execute(List<Rule> rules, RuleContext context) {
        // 按优先级排序
        rules.sort(Comparator.comparingInt(Rule::getPriority));

        Object result = null;
        for (Rule rule : rules) {
            result = execute(rule, context);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean validate(Rule rule) {
        try {
            // 验证条件表达式
            MVEL.compileExpression(rule.getCondition());
            // 验证动作表达式
            MVEL.compileExpression(rule.getAction());
            return true;
        } catch (Exception e) {
            log.error("规则验证失败: " + rule.getCode(), e);
            return false;
        }
    }

    @Override
    public void compile(Rule rule) {
        // 编译条件表达式
        Object compiledCondition = MVEL.compileExpression(rule.getCondition());
        conditionCache.put(rule.getId(), compiledCondition);

        // 编译动作表达式
        Object compiledAction = MVEL.compileExpression(rule.getAction());
        actionCache.put(rule.getId(), compiledAction);
    }

    private Object getCompiledCondition(Rule rule) {
        return conditionCache.computeIfAbsent(rule.getId(), k -> {
            log.debug("编译规则条件: {}", rule.getCode());
            return MVEL.compileExpression(rule.getCondition());
        });
    }

    private Object getCompiledAction(Rule rule) {
        return actionCache.computeIfAbsent(rule.getId(), k -> {
            log.debug("编译规则动作: {}", rule.getCode());
            return MVEL.compileExpression(rule.getAction());
        });
    }
} 