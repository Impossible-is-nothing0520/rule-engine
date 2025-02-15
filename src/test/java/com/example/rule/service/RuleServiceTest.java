package com.example.rule.service;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RuleServiceTest {
    @Autowired
    private RuleService ruleService;

    @Test
    @Transactional
    void testCreateAndExecuteRule() {
        // 创建规则
        Rule rule = new Rule()
                .setCode("discount_rule")
                .setName("折扣规则")
                .setDescription("VIP用户购买金额大于1000时享受8折优惠")
                .setCondition("userType == 'VIP' && amount >= 1000")
                .setAction("amount * 0.8")
                .setPriority(1);

        rule = ruleService.createRule(rule);
        assertNotNull(rule.getId());

        // 准备执行上下文
        Map<String, Object> params = new HashMap<>();
        params.put("userType", "VIP");
        params.put("amount", 2000.0);

        RuleContext context = new RuleContext();
        context.setParams(params);

        // 执行规则
        Object result = ruleService.executeRule(rule.getId(), context);
        assertNotNull(result);
        assertEquals(1600.0, ((Number) result).doubleValue());
    }

    @Test
    @Transactional
    void testMultipleRules() {
        // 创建VIP折扣规则
        Rule vipRule = new Rule()
                .setCode("vip_discount")
                .setName("VIP折扣")
                .setDescription("VIP用户享受8折优惠")
                .setCondition("userType == 'VIP'")
                .setAction("amount * 0.8")
                .setPriority(2);

        // 创建节假日折扣规则
        Rule holidayRule = new Rule()
                .setCode("holiday_discount")
                .setName("节假日折扣")
                .setDescription("节假日享受9折优惠")
                .setCondition("isHoliday == true")
                .setAction("amount * 0.9")
                .setPriority(1);

        vipRule = ruleService.createRule(vipRule);
        holidayRule = ruleService.createRule(holidayRule);

        // 准备执行上下文
        Map<String, Object> params = new HashMap<>();
        params.put("userType", "NORMAL");
        params.put("amount", 1000.0);
        params.put("isHoliday", true);

        RuleContext context = new RuleContext();
        context.setParams(params);

        // 执行所有规则
        Object result = ruleService.executeRules(java.util.Arrays.asList(vipRule.getId(), holidayRule.getId()), context);
        assertNotNull(result);
        assertEquals(900.0, ((Number) result).doubleValue());
    }

    @Test
    @Transactional
    void testInvalidRule() {
        // 创建无效规则
        Rule invalidRule = new Rule()
                .setCode("invalid_rule")
                .setName("无效规则")
                .setDescription("语法错误的规则")
                .setCondition("invalid syntax")
                .setAction("invalid action");

        // 验证规则应该失败
        assertThrows(IllegalArgumentException.class, () -> ruleService.createRule(invalidRule));
    }
} 