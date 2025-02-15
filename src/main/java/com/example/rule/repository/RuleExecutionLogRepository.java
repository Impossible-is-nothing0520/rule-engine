package com.example.rule.repository;

import com.example.rule.model.RuleExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RuleExecutionLogRepository extends JpaRepository<RuleExecutionLog, Long> {
    /**
     * 查询规则的执行日志
     */
    List<RuleExecutionLog> findByRuleId(Long ruleId);

    /**
     * 查询规则在指定时间范围内的执行日志
     */
    List<RuleExecutionLog> findByRuleIdAndExecutedAtBetween(Long ruleId, LocalDateTime start, LocalDateTime end);

    /**
     * 查询规则的执行成功率
     */
    @Query("SELECT COUNT(l) FROM RuleExecutionLog l WHERE l.ruleId = ?1 AND l.status = 'SUCCESS' / COUNT(l) * 100")
    Double calculateSuccessRate(Long ruleId);

    /**
     * 查询规则的平均执行时间
     */
    @Query("SELECT AVG(l.executionTime) FROM RuleExecutionLog l WHERE l.ruleId = ?1")
    Double calculateAverageExecutionTime(Long ruleId);
} 