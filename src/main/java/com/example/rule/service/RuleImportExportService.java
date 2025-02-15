package com.example.rule.service;

import com.example.rule.model.Rule;
import java.util.List;

/**
 * 规则导入导出服务接口
 */
public interface RuleImportExportService {
    /**
     * 导出规则为JSON
     */
    String exportRuleToJson(Long ruleId);

    /**
     * 导出多个规则为JSON
     */
    String exportRulesToJson(List<Long> ruleIds);

    /**
     * 从JSON导入规则
     */
    Rule importRuleFromJson(String json);

    /**
     * 从JSON导入多个规则
     */
    List<Rule> importRulesFromJson(String json);

    /**
     * 导出规则为Excel
     */
    byte[] exportRulesToExcel(List<Long> ruleIds);

    /**
     * 从Excel导入规则
     */
    List<Rule> importRulesFromExcel(byte[] excelData);
} 