package com.example.rule.repository;

import com.example.rule.model.RuleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleVersionRepository extends JpaRepository<RuleVersion, Long> {
    /**
     * 查询规则的所有版本
     */
    List<RuleVersion> findByRuleIdOrderByVersionDesc(Long ruleId);

    /**
     * 查询规则的最新版本
     */
    Optional<RuleVersion> findTopByRuleIdOrderByVersionDesc(Long ruleId);

    /**
     * 查询规则的指定版本
     */
    Optional<RuleVersion> findByRuleIdAndVersion(Long ruleId, Long version);

    /**
     * 获取规则的下一个版本号
     */
    @Query("SELECT COALESCE(MAX(v.version), 0) + 1 FROM RuleVersion v WHERE v.ruleId = ?1")
    Long getNextVersion(Long ruleId);
} 