package com.example.rule.service.impl;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleVersion;
import com.example.rule.repository.RuleVersionRepository;
import com.example.rule.service.RuleService;
import com.example.rule.service.RuleVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleVersionServiceImpl implements RuleVersionService {
    private final RuleVersionRepository versionRepository;
    private final ApplicationContext applicationContext;

    private RuleService getRuleService() {
        return applicationContext.getBean(RuleService.class);
    }

    @Override
    @Transactional
    public RuleVersion createVersion(Rule rule, String creator, String remark) {
        Long nextVersion = versionRepository.getNextVersion(rule.getId());
        
        RuleVersion version = new RuleVersion()
                .setRuleId(rule.getId())
                .setVersion(nextVersion)
                .setCode(rule.getCode())
                .setName(rule.getName())
                .setDescription(rule.getDescription())
                .setCondition(rule.getCondition())
                .setAction(rule.getAction())
                .setPriority(rule.getPriority())
                .setCreator(creator)
                .setRemark(remark);

        return versionRepository.save(version);
    }

    @Override
    public List<RuleVersion> getVersions(Long ruleId) {
        return versionRepository.findByRuleIdOrderByVersionDesc(ruleId);
    }

    @Override
    public RuleVersion getVersion(Long ruleId, Long version) {
        return versionRepository.findByRuleIdAndVersion(ruleId, version)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("规则版本不存在: ruleId=%d, version=%d", ruleId, version)));
    }

    @Override
    public RuleVersion getLatestVersion(Long ruleId) {
        return versionRepository.findTopByRuleIdOrderByVersionDesc(ruleId)
                .orElseThrow(() -> new EntityNotFoundException("规则版本不存在: " + ruleId));
    }

    @Override
    @Transactional
    public Rule rollbackToVersion(Long ruleId, Long version) {
        // 获取指定版本
        RuleVersion ruleVersion = getVersion(ruleId, version);
        
        // 获取当前规则
        Rule rule = getRuleService().getRule(ruleId);
        
        // 更新规则属性
        rule.setName(ruleVersion.getName())
            .setDescription(ruleVersion.getDescription())
            .setCondition(ruleVersion.getCondition())
            .setAction(ruleVersion.getAction())
            .setPriority(ruleVersion.getPriority());

        // 保存更新后的规则
        return getRuleService().updateRule(rule);
    }

    @Override
    public String compareVersions(Long ruleId, Long version1, Long version2) {
        RuleVersion v1 = getVersion(ruleId, version1);
        RuleVersion v2 = getVersion(ruleId, version2);

        StringBuilder diff = new StringBuilder();
        diff.append("版本比较: ").append(version1).append(" vs ").append(version2).append("\n");

        if (!v1.getName().equals(v2.getName())) {
            diff.append("名称: ").append(v1.getName()).append(" -> ").append(v2.getName()).append("\n");
        }
        if (!v1.getDescription().equals(v2.getDescription())) {
            diff.append("描述: ").append(v1.getDescription()).append(" -> ").append(v2.getDescription()).append("\n");
        }
        if (!v1.getCondition().equals(v2.getCondition())) {
            diff.append("条件: ").append(v1.getCondition()).append(" -> ").append(v2.getCondition()).append("\n");
        }
        if (!v1.getAction().equals(v2.getAction())) {
            diff.append("动作: ").append(v1.getAction()).append(" -> ").append(v2.getAction()).append("\n");
        }
        if (v1.getPriority() != v2.getPriority()) {
            diff.append("优先级: ").append(v1.getPriority()).append(" -> ").append(v2.getPriority()).append("\n");
        }

        return diff.toString();
    }
} 