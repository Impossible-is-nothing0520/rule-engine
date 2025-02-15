package com.example.rule.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.rule.model.Rule;
import com.example.rule.model.RuleTemplate;
import com.example.rule.repository.RuleTemplateRepository;
import com.example.rule.service.RuleService;
import com.example.rule.service.RuleTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleTemplateServiceImpl implements RuleTemplateService {
    private final RuleTemplateRepository templateRepository;
    private final RuleService ruleService;

    @Override
    @Transactional
    public RuleTemplate createTemplate(RuleTemplate template) {
        return templateRepository.save(template);
    }

    @Override
    @Transactional
    public RuleTemplate updateTemplate(RuleTemplate template) {
        RuleTemplate existingTemplate = getTemplate(template.getId());
        
        existingTemplate.setName(template.getName())
                .setDescription(template.getDescription())
                .setConditionTemplate(template.getConditionTemplate())
                .setActionTemplate(template.getActionTemplate())
                .setParameterDefinitions(template.getParameterDefinitions());

        return templateRepository.save(existingTemplate);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        templateRepository.deleteById(id);
    }

    @Override
    public RuleTemplate getTemplate(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在: " + id));
    }

    @Override
    public RuleTemplate getTemplateByCode(String code) {
        return templateRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("模板不存在: " + code));
    }

    @Override
    public List<RuleTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    @Transactional
    public Rule createRuleFromTemplate(Long templateId, Map<String, Object> parameters) {
        // 获取模板
        RuleTemplate template = getTemplate(templateId);
        
        // 验证参数
        if (!validateParameters(templateId, parameters)) {
            throw new IllegalArgumentException("参数验证失败");
        }

        // 替换模板中的参数
        StringSubstitutor substitutor = new StringSubstitutor(parameters);
        String condition = substitutor.replace(template.getConditionTemplate());
        String action = substitutor.replace(template.getActionTemplate());

        // 创建规则
        Rule rule = new Rule()
                .setCode(template.getCode() + "_" + System.currentTimeMillis())
                .setName(template.getName())
                .setDescription(template.getDescription())
                .setCondition(condition)
                .setAction(action);

        return ruleService.createRule(rule);
    }

    @Override
    public boolean validateParameters(Long templateId, Map<String, Object> parameters) {
        RuleTemplate template = getTemplate(templateId);
        
        try {
            JSONObject paramDefs = JSON.parseObject(template.getParameterDefinitions());
            
            // 检查必需参数是否存在
            for (String key : paramDefs.keySet()) {
                JSONObject paramDef = paramDefs.getJSONObject(key);
                if (paramDef.getBooleanValue("required") && !parameters.containsKey(key)) {
                    log.error("缺少必需参数: {}", key);
                    return false;
                }

                // 检查参数类型
                if (parameters.containsKey(key)) {
                    String type = paramDef.getString("type");
                    Object value = parameters.get(key);
                    if (!validateParameterType(value, type)) {
                        log.error("参数类型不匹配: {}, 期望类型: {}", key, type);
                        return false;
                    }
                }
            }
            
            return true;
        } catch (Exception e) {
            log.error("参数验证失败", e);
            return false;
        }
    }

    private boolean validateParameterType(Object value, String type) {
        if (value == null) return false;
        
        switch (type.toLowerCase()) {
            case "string":
                return value instanceof String;
            case "number":
                return value instanceof Number;
            case "boolean":
                return value instanceof Boolean;
            default:
                return false;
        }
    }
} 