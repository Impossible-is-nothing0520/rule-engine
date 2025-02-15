package com.example.rule.controller;

import com.example.rule.model.Rule;
import com.example.rule.service.RuleImportExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rule-import-export")
@RequiredArgsConstructor
public class RuleImportExportController {
    private final RuleImportExportService importExportService;

    @GetMapping("/export/json/{ruleId}")
    public ResponseEntity<String> exportRuleToJson(@PathVariable Long ruleId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(importExportService.exportRuleToJson(ruleId));
    }

    @PostMapping("/export/json/batch")
    public ResponseEntity<String> exportRulesToJson(@RequestBody List<Long> ruleIds) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(importExportService.exportRulesToJson(ruleIds));
    }

    @PostMapping("/import/json")
    public ResponseEntity<Rule> importRuleFromJson(@RequestBody String json) {
        return ResponseEntity.ok(importExportService.importRuleFromJson(json));
    }

    @PostMapping("/import/json/batch")
    public ResponseEntity<List<Rule>> importRulesFromJson(@RequestBody String json) {
        return ResponseEntity.ok(importExportService.importRulesFromJson(json));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportRulesToExcel(@RequestParam List<Long> ruleIds) {
        byte[] excelData = importExportService.exportRulesToExcel(ruleIds);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rules.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(excelData.length)
                .body(excelData);
    }

    @PostMapping("/import/excel")
    public ResponseEntity<List<Rule>> importRulesFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(importExportService.importRulesFromExcel(file.getBytes()));
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败", e);
        }
    }
} 