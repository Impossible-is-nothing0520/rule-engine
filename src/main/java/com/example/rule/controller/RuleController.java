package com.example.rule.controller;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleContext;
import com.example.rule.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {
    private final RuleService ruleService;

    @PostMapping
    public ResponseEntity<Rule> createRule(@RequestBody Rule rule) {
        return ResponseEntity.ok(ruleService.createRule(rule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody Rule rule) {
        rule.setId(id);
        return ResponseEntity.ok(ruleService.updateRule(rule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<Rule> enableRule(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.enableRule(id));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<Rule> disableRule(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.disableRule(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRule(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRule(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Rule> getRuleByCode(@PathVariable String code) {
        return ResponseEntity.ok(ruleService.getRuleByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<Rule>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @GetMapping("/enabled")
    public ResponseEntity<List<Rule>> getEnabledRules() {
        return ResponseEntity.ok(ruleService.getEnabledRules());
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<Object> executeRule(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        RuleContext context = new RuleContext();
        context.setParams(params);
        return ResponseEntity.ok(ruleService.executeRule(id, context));
    }

    @PostMapping("/execute-batch")
    public ResponseEntity<Object> executeRules(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ruleIds = (List<Long>) request.get("ruleIds");
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.get("params");

        RuleContext context = new RuleContext();
        context.setParams(params);
        return ResponseEntity.ok(ruleService.executeRules(ruleIds, context));
    }
} 