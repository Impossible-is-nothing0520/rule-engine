package com.example.rule.service.impl;

import com.example.rule.engine.RuleEngine;
import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;
import com.example.rule.model.RuleStatus;
import com.example.rule.repository.RuleRepository;
import com.example.rule.service.RuleService;
import com.example.rule.service.RuleVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleServiceImpl implements RuleService {
    private final RuleRepository ruleRepository;
    private final RuleEngine ruleEngine;
    private final RuleVersionService versionService;

    @Override
    @Transactional
    public Rule createRule(Rule rule) {
        // 验证规则
        if (!validateRule(rule)) {
            throw new IllegalArgumentException("规则验证失败");
        }
        
        // 保存规则
        rule = ruleRepository.save(rule);

        // 编译规则
        ruleEngine.compile(rule);

        // 创建初始版本
        versionService.createVersion(rule, "system", "初始版本");
        
        return rule;
    }

    @Override
    @Transactional
    public Rule updateRule(Rule rule) {
        // 检查规则是否存在
        Rule existingRule = getRule(rule.getId());
        
        // 验证规则
        if (!validateRule(rule)) {
            throw new IllegalArgumentException("规则验证失败");
        }

        // 更新规则属性
        existingRule.setName(rule.getName())
                .setDescription(rule.getDescription())
                .setCondition(rule.getCondition())
                .setAction(rule.getAction())
                .setPriority(rule.getPriority());

        // 编译规则
        ruleEngine.compile(existingRule);
        
        // 保存规则
        existingRule = ruleRepository.save(existingRule);

        // 创建新版本
        versionService.createVersion(existingRule, "system", "规则更新");
        
        return existingRule;
    }

    @Override
    @Transactional
    public void deleteRule(Long id) {
        ruleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Rule enableRule(Long id) {
        Rule rule = getRule(id);
        rule.setStatus(RuleStatus.ENABLED);
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public Rule disableRule(Long id) {
        Rule rule = getRule(id);
        rule.setStatus(RuleStatus.DISABLED);
        return ruleRepository.save(rule);
    }

    @Override
    public Rule getRule(Long id) {
        return ruleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("规则不存在: " + id));
    }

    @Override
    public Rule getRuleByCode(String code) {
        return ruleRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("规则不存在: " + code));
    }

    @Override
    public List<Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @Override
    public List<Rule> getEnabledRules() {
        return ruleRepository.findByStatus(RuleStatus.ENABLED);
    }

    @Override
    public Object executeRule(Long id, RuleContext context) {
        Rule rule = getRule(id);
        return ruleEngine.execute(rule, context);
    }

    @Override
    public Object executeRules(List<Long> ids, RuleContext context) {
        List<Rule> rules = ruleRepository.findAllById(ids);
        return ruleEngine.execute(rules, context);
    }

    @Override
    public boolean validateRule(Rule rule) {
        try {
            return ruleEngine.validate(rule);
        } catch (Exception e) {
            log.error("规则验证异常", e);
            return false;
        }
    }
} 