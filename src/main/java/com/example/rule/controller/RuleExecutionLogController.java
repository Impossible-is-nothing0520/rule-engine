package com.example.rule.controller;

import com.example.rule.model.RuleExecutionLog;
import com.example.rule.service.RuleExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/rule-logs")
@RequiredArgsConstructor
public class RuleExecutionLogController {
    private final RuleExecutionLogService logService;

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RuleExecutionLog>> getRuleLogs(
            @PathVariable Long ruleId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (start != null && end != null) {
            return ResponseEntity.ok(logService.getRuleLogs(ruleId, start, end));
        }
        return ResponseEntity.ok(logService.getRuleLogs(ruleId));
    }

    @GetMapping("/rule/{ruleId}/success-rate")
    public ResponseEntity<Double> getSuccessRate(@PathVariable Long ruleId) {
        return ResponseEntity.ok(logService.getSuccessRate(ruleId));
    }

    @GetMapping("/rule/{ruleId}/avg-execution-time")
    public ResponseEntity<Double> getAverageExecutionTime(@PathVariable Long ruleId) {
        return ResponseEntity.ok(logService.getAverageExecutionTime(ruleId));
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime before) {
        logService.cleanupLogs(before);
        return ResponseEntity.ok().build();
    }
} 