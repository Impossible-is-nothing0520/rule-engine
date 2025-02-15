package com.example.rule.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 规则实体
 */
@Data
@Entity
@Table(name = "rules")
@Accessors(chain = true)
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则代码
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 规则名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 规则描述
     */
    private String description;

    /**
     * 规则条件（MVEL表达式）
     */
    @Column(nullable = false, name = "`condition`")
    @Type(type = "text")
    private String condition;

    /**
     * 规则动作（MVEL表达式）
     */
    @Column(nullable = false)
    @Type(type = "text")
    private String action;

    /**
     * 优先级（数字越小优先级越高）
     */
    private int priority = 100;

    /**
     * 规则状态（ENABLED/DISABLED）
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleStatus status = RuleStatus.ENABLED;

    /**
     * 规则版本
     */
    @Version
    private Long version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
} 