package com.example.rule.engine;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;

import java.util.List;

/**
 * 规则引擎接口
 */
public interface RuleEngine {
    /**
     * 执行单个规则
     *
     * @param rule    规则
     * @param context 上下文
     * @return 执行结果
     */
    Object execute(Rule rule, RuleContext context);

    /**
     * 执行多个规则
     *
     * @param rules   规则列表
     * @param context 上下文
     * @return 执行结果
     */
    Object execute(List<Rule> rules, RuleContext context);

    /**
     * 验证规则是否有效
     *
     * @param rule 规则
     * @return 是否有效
     */
    boolean validate(Rule rule);

    /**
     * 编译规则
     *
     * @param rule 规则
     */
    void compile(Rule rule);
} 