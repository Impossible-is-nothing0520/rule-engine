package com.example.rule.controller;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleTemplate;
import com.example.rule.service.RuleTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rule-templates")
@RequiredArgsConstructor
public class RuleTemplateController {
    private final RuleTemplateService templateService;

    @PostMapping
    public ResponseEntity<RuleTemplate> createTemplate(@RequestBody RuleTemplate template) {
        return ResponseEntity.ok(templateService.createTemplate(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleTemplate> updateTemplate(
            @PathVariable Long id,
            @RequestBody RuleTemplate template) {
        template.setId(id);
        return ResponseEntity.ok(templateService.updateTemplate(template));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleTemplate> getTemplate(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplate(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<RuleTemplate> getTemplateByCode(@PathVariable String code) {
        return ResponseEntity.ok(templateService.getTemplateByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<RuleTemplate>> getAllTemplates() {
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @PostMapping("/{id}/create-rule")
    public ResponseEntity<Rule> createRuleFromTemplate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> parameters) {
        return ResponseEntity.ok(templateService.createRuleFromTemplate(id, parameters));
    }

    @PostMapping("/{id}/validate-parameters")
    public ResponseEntity<Boolean> validateParameters(
            @PathVariable Long id,
            @RequestBody Map<String, Object> parameters) {
        return ResponseEntity.ok(templateService.validateParameters(id, parameters));
    }
} 