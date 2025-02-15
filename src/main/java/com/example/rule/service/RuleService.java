package com.example.rule.service;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;

import java.util.List;

/**
 * 规则服务接口
 */
public interface RuleService {
    /**
     * 创建规则
     */
    Rule createRule(Rule rule);

    /**
     * 更新规则
     */
    Rule updateRule(Rule rule);

    /**
     * 删除规则
     */
    void deleteRule(Long id);

    /**
     * 启用规则
     */
    Rule enableRule(Long id);

    /**
     * 禁用规则
     */
    Rule disableRule(Long id);

    /**
     * 获取规则详情
     */
    Rule getRule(Long id);

    /**
     * 根据代码获取规则
     */
    Rule getRuleByCode(String code);

    /**
     * 获取所有规则
     */
    List<Rule> getAllRules();

    /**
     * 获取所有启用的规则
     */
    List<Rule> getEnabledRules();

    /**
     * 执行单个规则
     */
    Object executeRule(Long id, RuleContext context);

    /**
     * 执行多个规则
     */
    Object executeRules(List<Long> ids, RuleContext context);

    /**
     * 验证规则
     */
    boolean validateRule(Rule rule);
} 