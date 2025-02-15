package com.example.rule.config;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleStatus;
import com.example.rule.model.RuleTemplate;
import com.example.rule.repository.RuleExecutionLogRepository;
import com.example.rule.repository.RuleRepository;
import com.example.rule.repository.RuleTemplateRepository;
import com.example.rule.repository.RuleVersionRepository;
import com.example.rule.service.RuleService;
import com.example.rule.service.RuleTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RuleService ruleService;
    private final RuleTemplateService templateService;
    private final RuleRepository ruleRepository;
    private final RuleTemplateRepository templateRepository;
    private final RuleVersionRepository versionRepository;
    private final RuleExecutionLogRepository executionLogRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        clearData();
        initializeTemplates();
        initializeRules();
    }

    private void clearData() {
        try {
            log.info("开始清除历史数据...");
            
            // 禁用外键检查
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
            
            // 清除所有表数据
            executionLogRepository.deleteAll();
            versionRepository.deleteAll();
            ruleRepository.deleteAll();
            templateRepository.deleteAll();
            
            // 启用外键检查
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
            
            log.info("历史数据清除完成");
        } catch (Exception e) {
            log.error("清除历史数据失败", e);
            // 确保外键检查被重新启用
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private void initializeTemplates() {
        try {
            // 折扣规则模板
            RuleTemplate discountTemplate = new RuleTemplate()
                    .setCode("discount_template")
                    .setName("折扣规则模板")
                    .setDescription("根据用户类型和订单金额计算折扣")
                    .setConditionTemplate("userType == '${userType}' && amount >= ${minAmount}")
                    .setActionTemplate("amount * ${discountRate}")
                    .setParameterDefinitions("""
                            {
                                "userType": {"type": "string", "required": true, "description": "用户类型"},
                                "minAmount": {"type": "number", "required": true, "description": "最小订单金额"},
                                "discountRate": {"type": "number", "required": true, "description": "折扣率"}
                            }
                            """);
            templateService.createTemplate(discountTemplate);

            // 积分规则模板
            RuleTemplate pointsTemplate = new RuleTemplate()
                    .setCode("points_template")
                    .setName("积分规则模板")
                    .setDescription("根据订单金额计算积分")
                    .setConditionTemplate("amount >= ${minAmount}")
                    .setActionTemplate("Math.floor(amount * ${pointsRate})")
                    .setParameterDefinitions("""
                            {
                                "minAmount": {"type": "number", "required": true, "description": "最小订单金额"},
                                "pointsRate": {"type": "number", "required": true, "description": "积分倍率"}
                            }
                            """);
            templateService.createTemplate(pointsTemplate);

            log.info("模板初始化完成");
        } catch (Exception e) {
            log.error("模板初始化失败", e);
        }
    }

    private void initializeRules() {
        try {
            // VIP用户折扣规则
            Rule vipRule = new Rule()
                    .setCode("vip_discount")
                    .setName("VIP用户折扣")
                    .setDescription("VIP用户订单满1000享受8折优惠")
                    .setCondition("userType == 'VIP' && amount >= 1000")
                    .setAction("amount * 0.8")
                    .setPriority(1)
                    .setStatus(RuleStatus.ENABLED);
            ruleService.createRule(vipRule);

            // 普通用户折扣规则
            Rule normalRule = new Rule()
                    .setCode("normal_discount")
                    .setName("普通用户折扣")
                    .setDescription("普通用户订单满2000享受9折优惠")
                    .setCondition("userType == 'NORMAL' && amount >= 2000")
                    .setAction("amount * 0.9")
                    .setPriority(2)
                    .setStatus(RuleStatus.ENABLED);
            ruleService.createRule(normalRule);

            // 节假日折扣规则
            Rule holidayRule = new Rule()
                    .setCode("holiday_discount")
                    .setName("节假日折扣")
                    .setDescription("节假日所有订单享受95折优惠")
                    .setCondition("isHoliday == true")
                    .setAction("amount * 0.95")
                    .setPriority(3)
                    .setStatus(RuleStatus.ENABLED);
            ruleService.createRule(holidayRule);

            // 积分规则
            Rule pointsRule = new Rule()
                    .setCode("order_points")
                    .setName("订单积分")
                    .setDescription("订单金额每100元得10积分")
                    .setCondition("amount >= 100")
                    .setAction("Math.floor(amount / 100) * 10")
                    .setPriority(4)
                    .setStatus(RuleStatus.ENABLED);
            ruleService.createRule(pointsRule);

            log.info("规则初始化完成");
        } catch (Exception e) {
            log.error("规则初始化失败", e);
        }
    }
} 