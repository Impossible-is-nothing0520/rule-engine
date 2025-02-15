package com.example.rule.repository;

import com.example.rule.model.RuleTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuleTemplateRepository extends JpaRepository<RuleTemplate, Long> {
    /**
     * 根据模板代码查询模板
     */
    Optional<RuleTemplate> findByCode(String code);
} 