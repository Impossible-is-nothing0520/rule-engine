package com.example.rule.service.impl;

import com.example.rule.model.RuleExecutionLog;
import com.example.rule.repository.RuleExecutionLogRepository;
import com.example.rule.service.RuleExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RuleExecutionLogServiceImpl implements RuleExecutionLogService {
    private final RuleExecutionLogRepository logRepository;

    @Override
    @Transactional
    public RuleExecutionLog logExecution(RuleExecutionLog log) {
        return logRepository.save(log);
    }

    @Override
    public List<RuleExecutionLog> getRuleLogs(Long ruleId) {
        return logRepository.findByRuleId(ruleId);
    }

    @Override
    public List<RuleExecutionLog> getRuleLogs(Long ruleId, LocalDateTime start, LocalDateTime end) {
        return logRepository.findByRuleIdAndExecutedAtBetween(ruleId, start, end);
    }

    @Override
    public double getSuccessRate(Long ruleId) {
        Double rate = logRepository.calculateSuccessRate(ruleId);
        return rate != null ? rate : 0.0;
    }

    @Override
    public double getAverageExecutionTime(Long ruleId) {
        Double avgTime = logRepository.calculateAverageExecutionTime(ruleId);
        return avgTime != null ? avgTime : 0.0;
    }

    @Override
    @Transactional
    public void cleanupLogs(LocalDateTime before) {
        // 这里可以实现日志清理逻辑
        // 例如：删除指定时间之前的日志
    }
} 