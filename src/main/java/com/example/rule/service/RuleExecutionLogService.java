package com.example.rule.service;

import com.example.rule.model.RuleExecutionLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 规则执行日志服务接口
 */
public interface RuleExecutionLogService {
    /**
     * 记录规则执行日志
     */
    RuleExecutionLog logExecution(RuleExecutionLog log);

    /**
     * 获取规则执行日志
     */
    List<RuleExecutionLog> getRuleLogs(Long ruleId);

    /**
     * 获取规则在指定时间范围内的执行日志
     */
    List<RuleExecutionLog> getRuleLogs(Long ruleId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取规则执行成功率
     */
    double getSuccessRate(Long ruleId);

    /**
     * 获取规则平均执行时间
     */
    double getAverageExecutionTime(Long ruleId);

    /**
     * 清理指定时间之前的日志
     */
    void cleanupLogs(LocalDateTime before);
} 