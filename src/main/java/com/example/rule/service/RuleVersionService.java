package com.example.rule.service;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleVersion;

import java.util.List;

/**
 * 规则版本服务接口
 */
public interface RuleVersionService {
    /**
     * 创建规则版本
     */
    RuleVersion createVersion(Rule rule, String creator, String remark);

    /**
     * 获取规则的所有版本
     */
    List<RuleVersion> getVersions(Long ruleId);

    /**
     * 获取规则的指定版本
     */
    RuleVersion getVersion(Long ruleId, Long version);

    /**
     * 获取规则的最新版本
     */
    RuleVersion getLatestVersion(Long ruleId);

    /**
     * 回滚到指定版本
     */
    Rule rollbackToVersion(Long ruleId, Long version);

    /**
     * 比较两个版本的差异
     */
    String compareVersions(Long ruleId, Long version1, Long version2);
} 