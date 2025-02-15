package com.example.rule.controller;

import com.example.rule.model.Rule;
import com.example.rule.model.RuleVersion;
import com.example.rule.service.RuleVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rule-versions")
@RequiredArgsConstructor
public class RuleVersionController {
    private final RuleVersionService versionService;

    @GetMapping("/rule/{ruleId}")
    public ResponseEntity<List<RuleVersion>> getVersions(@PathVariable Long ruleId) {
        return ResponseEntity.ok(versionService.getVersions(ruleId));
    }

    @GetMapping("/rule/{ruleId}/version/{version}")
    public ResponseEntity<RuleVersion> getVersion(
            @PathVariable Long ruleId,
            @PathVariable Long version) {
        return ResponseEntity.ok(versionService.getVersion(ruleId, version));
    }

    @GetMapping("/rule/{ruleId}/latest")
    public ResponseEntity<RuleVersion> getLatestVersion(@PathVariable Long ruleId) {
        return ResponseEntity.ok(versionService.getLatestVersion(ruleId));
    }

    @PostMapping("/rule/{ruleId}/rollback/{version}")
    public ResponseEntity<Rule> rollbackToVersion(
            @PathVariable Long ruleId,
            @PathVariable Long version) {
        return ResponseEntity.ok(versionService.rollbackToVersion(ruleId, version));
    }

    @GetMapping("/rule/{ruleId}/compare")
    public ResponseEntity<String> compareVersions(
            @PathVariable Long ruleId,
            @RequestParam Long version1,
            @RequestParam Long version2) {
        return ResponseEntity.ok(versionService.compareVersions(ruleId, version1, version2));
    }
} 