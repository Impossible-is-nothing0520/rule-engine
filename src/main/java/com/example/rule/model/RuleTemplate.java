package com.example.rule.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rule_templates")
@Accessors(chain = true)
public class RuleTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 模板代码
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * 模板名称
     */
    @Column(nullable = false)
    private String name;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 条件模板
     */
    @Column(nullable = false, columnDefinition = "TEXT", name = "`condition_template`")
    private String conditionTemplate;

    /**
     * 动作模板
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String actionTemplate;

    /**
     * 参数定义（JSON格式）
     */
    @Column(columnDefinition = "TEXT")
    private String parameterDefinitions;

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