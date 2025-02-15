package com.example.rule.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rule_execution_logs")
@Accessors(chain = true)
public class RuleExecutionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则ID
     */
    @Column(nullable = false)
    private Long ruleId;

    /**
     * 规则代码
     */
    @Column(nullable = false)
    private String ruleCode;

    /**
     * 执行结果（SUCCESS/FAILED）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    /**
     * 输入参数（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String input;

    /**
     * 输出结果（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String output;

    /**
     * 错误信息
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 执行时间（毫秒）
     */
    private Long executionTime;

    /**
     * 执行时间
     */
    private LocalDateTime executedAt;

    @PrePersist
    public void prePersist() {
        executedAt = LocalDateTime.now();
    }

    public enum ExecutionStatus {
        SUCCESS, FAILED
    }
} 