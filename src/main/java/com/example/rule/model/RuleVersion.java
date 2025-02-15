package com.example.rule.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rule_versions")
@Accessors(chain = true)
public class RuleVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 规则ID
     */
    @Column(nullable = false)
    private Long ruleId;

    /**
     * 版本号
     */
    @Column(nullable = false)
    private Long version;

    /**
     * 规则代码
     */
    @Column(nullable = false)
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
     * 规则条件
     */
    @Column(nullable = false, columnDefinition = "TEXT", name = "`condition`")
    private String condition;

    /**
     * 规则动作
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String action;

    /**
     * 优先级
     */
    private int priority;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 备注
     */
    private String remark;

    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
    }
} 