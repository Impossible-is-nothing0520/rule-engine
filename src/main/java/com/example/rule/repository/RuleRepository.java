package com.example.rule.repository;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
    /**
     * 根据规则代码查询规则
     */
    Optional<Rule> findByCode(String code);

    /**
     * 查询所有启用的规则
     */
    List<Rule> findByStatus(RuleStatus status);

    /**
     * 根据优先级范围查询规则
     */
    List<Rule> findByPriorityBetweenOrderByPriorityAsc(int minPriority, int maxPriority);

    /**
     * 查询最新修改的规则
     */
    @Query("SELECT r FROM Rule r ORDER BY r.updateTime DESC")
    List<Rule> findLatestModified();
} 