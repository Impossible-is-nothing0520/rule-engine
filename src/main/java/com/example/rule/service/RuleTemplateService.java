package com.example.rule.service;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleTemplate;

import java.util.List;
import java.util.Map;

/**
 * 规则模板服务接口
 */
public interface RuleTemplateService {
    /**
     * 创建模板
     */
    RuleTemplate createTemplate(RuleTemplate template);

    /**
     * 更新模板
     */
    RuleTemplate updateTemplate(RuleTemplate template);

    /**
     * 删除模板
     */
    void deleteTemplate(Long id);

    /**
     * 获取模板
     */
    RuleTemplate getTemplate(Long id);

    /**
     * 根据代码获取模板
     */
    RuleTemplate getTemplateByCode(String code);

    /**
     * 获取所有模板
     */
    List<RuleTemplate> getAllTemplates();

    /**
     * 使用模板创建规则
     */
    Rule createRuleFromTemplate(Long templateId, Map<String, Object> parameters);

    /**
     * 验证参数是否符合模板定义
     */
    boolean validateParameters(Long templateId, Map<String, Object> parameters);
} 