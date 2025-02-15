package com.example.rule.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.rule.model.Rule;
import com.example.rule.service.RuleImportExportService;
import com.example.rule.service.RuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleImportExportServiceImpl implements RuleImportExportService {
    private final RuleService ruleService;

    private static final String[] EXCEL_HEADERS = {
        "规则代码", "规则名称", "规则描述", "规则条件", "规则动作", "优先级", "状态"
    };

    @Override
    public String exportRuleToJson(Long ruleId) {
        Rule rule = ruleService.getRule(ruleId);
        return JSON.toJSONString(rule);
    }

    @Override
    public String exportRulesToJson(List<Long> ruleIds) {
        List<Rule> rules = new ArrayList<>();
        for (Long ruleId : ruleIds) {
            rules.add(ruleService.getRule(ruleId));
        }
        return JSON.toJSONString(rules);
    }

    @Override
    @Transactional
    public Rule importRuleFromJson(String json) {
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            Rule rule = new Rule()
                    .setCode(jsonObject.getString("code"))
                    .setName(jsonObject.getString("name"))
                    .setDescription(jsonObject.getString("description"))
                    .setCondition(jsonObject.getString("condition"))
                    .setAction(jsonObject.getString("action"))
                    .setPriority(jsonObject.getIntValue("priority"));

            return ruleService.createRule(rule);
        } catch (Exception e) {
            log.error("导入规则失败", e);
            throw new RuntimeException("导入规则失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Rule> importRulesFromJson(String json) {
        try {
            List<Rule> rules = new ArrayList<>();
            JSONArray jsonArray = JSON.parseArray(json);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Rule rule = new Rule()
                        .setCode(jsonObject.getString("code"))
                        .setName(jsonObject.getString("name"))
                        .setDescription(jsonObject.getString("description"))
                        .setCondition(jsonObject.getString("condition"))
                        .setAction(jsonObject.getString("action"))
                        .setPriority(jsonObject.getIntValue("priority"));

                rules.add(ruleService.createRule(rule));
            }

            return rules;
        } catch (Exception e) {
            log.error("批量导入规则失败", e);
            throw new RuntimeException("批量导入规则失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] exportRulesToExcel(List<Long> ruleIds) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("规则列表");

            // 创建标题行样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(EXCEL_HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 写入数据行
            int rowNum = 1;
            for (Long ruleId : ruleIds) {
                Rule rule = ruleService.getRule(ruleId);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rule.getCode());
                row.createCell(1).setCellValue(rule.getName());
                row.createCell(2).setCellValue(rule.getDescription());
                row.createCell(3).setCellValue(rule.getCondition());
                row.createCell(4).setCellValue(rule.getAction());
                row.createCell(5).setCellValue(rule.getPriority());
                row.createCell(6).setCellValue(rule.getStatus().name());
            }

            // 自动调整列宽
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 输出Excel数据
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<Rule> importRulesFromExcel(byte[] excelData) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelData))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Rule> rules = new ArrayList<>();

            // 从第二行开始读取数据（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Rule rule = new Rule()
                            .setCode(getCellStringValue(row.getCell(0)))
                            .setName(getCellStringValue(row.getCell(1)))
                            .setDescription(getCellStringValue(row.getCell(2)))
                            .setCondition(getCellStringValue(row.getCell(3)))
                            .setAction(getCellStringValue(row.getCell(4)))
                            .setPriority(getCellIntValue(row.getCell(5)));

                    rules.add(ruleService.createRule(rule));
                } catch (Exception e) {
                    log.error("导入第{}行数据失败: {}", i + 1, e.getMessage());
                }
            }

            return rules;
        } catch (IOException e) {
            log.error("导入Excel失败", e);
            throw new RuntimeException("导入Excel失败: " + e.getMessage());
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private int getCellIntValue(Cell cell) {
        if (cell == null) return 0;

        cell.setCellType(CellType.NUMERIC);
        return (int) cell.getNumericCellValue();
    }
} 