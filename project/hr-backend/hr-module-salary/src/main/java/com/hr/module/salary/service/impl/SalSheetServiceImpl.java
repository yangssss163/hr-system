package com.hr.module.salary.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.salary.dto.*;
import com.hr.module.salary.entity.*;
import com.hr.module.salary.mapper.*;
import com.hr.module.salary.service.SalSheetService;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.mapper.SysDeptMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalSheetServiceImpl implements SalSheetService {

    private final SalSheetMapper salSheetMapper;
    private final SalRuleMapper salRuleMapper;
    private final SalFieldMapper salFieldMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // status 语义: 0=草稿  1=已生成  2=已导出

    @Override
    public IPage<SalSheetVO> page(SalSheetQuery query) {
        LambdaQueryWrapper<SalSheet> wrapper = new LambdaQueryWrapper<>();
        if (query.getMonth() != null && !query.getMonth().isEmpty()) {
            wrapper.eq(SalSheet::getMonth, query.getMonth());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SalSheet::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SalSheet::getId);
        Page<SalSheet> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SalSheet> result = salSheetMapper.selectPage(page, wrapper);

        List<Long> empIds = result.getRecords().stream()
                .map(SalSheet::getEmployeeId).distinct().collect(Collectors.toList());
        Map<Long, HrEmployee> empMap = empIds.isEmpty() ? Map.of() :
                hrEmployeeMapper.selectBatchIds(empIds).stream()
                        .collect(Collectors.toMap(HrEmployee::getId, e -> e));
        List<Long> deptIds = empMap.values().stream()
                .map(HrEmployee::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));

        // 查询启用字段元数据
        List<SalField> enabledFields = salFieldMapper.selectList(
                new LambdaQueryWrapper<SalField>()
                        .eq(SalField::getStatus, 1)
                        .orderByAsc(SalField::getSort));
        Map<String, SalField> fieldMap = enabledFields.stream()
                .collect(Collectors.toMap(SalField::getCode, f -> f));

        return result.convert(sheet -> toVO(sheet, empMap, deptNameMap, fieldMap));
    }

    @Override
    @Transactional
    public void sync(SalSheetSyncDTO dto) {
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HrEmployee::getStatus, 1);
        if (dto.getDeptId() != null) {
            wrapper.eq(HrEmployee::getDeptId, dto.getDeptId());
        }

        List<HrEmployee> employees = hrEmployeeMapper.selectList(wrapper);
        int created = 0;
        int updated = 0;
        int skipped = 0;

        for (HrEmployee emp : employees) {
            SalSheet existing = salSheetMapper.selectOne(
                    new LambdaQueryWrapper<SalSheet>()
                            .eq(SalSheet::getEmployeeId, emp.getId())
                            .eq(SalSheet::getMonth, dto.getMonth()));

            if (existing != null) {
                if (existing.getStatus() >= 1) {
                    // 已生成或已导出 → 不可再同步
                    skipped++;
                    continue;
                }
                // status=0 草稿 → 更新底薪（员工底薪可能已调）
                BigDecimal empBase = emp.getBaseSalary() != null ? emp.getBaseSalary() : BigDecimal.ZERO;
                existing.setBaseSalary(empBase);
                salSheetMapper.updateById(existing);
                updated++;
                continue;
            }

            // 新建草稿
            BigDecimal empBase = emp.getBaseSalary() != null ? emp.getBaseSalary() : BigDecimal.ZERO;
            SalSheet sheet = new SalSheet();
            sheet.setEmployeeId(emp.getId());
            sheet.setMonth(dto.getMonth());
            sheet.setBaseSalary(empBase);
            sheet.setPerfSalary(BigDecimal.ZERO);
            sheet.setSubsidy(BigDecimal.ZERO);
            sheet.setOvertimePay(BigDecimal.ZERO);
            sheet.setTotalIncome(BigDecimal.ZERO);
            sheet.setSocialInsurance(BigDecimal.ZERO);
            sheet.setTax(BigDecimal.ZERO);
            sheet.setTotalDeduction(BigDecimal.ZERO);
            sheet.setNetSalary(BigDecimal.ZERO);
            sheet.setStatus(0);
            salSheetMapper.insert(sheet);
            created++;
        }

        log.info("同步完成: month={}, 新增 {} 条, 更新 {} 条, 跳过 {} 条(已生成/已导出)", dto.getMonth(), created, updated, skipped);
    }

    @Override
    @Transactional
    public void generate(SalSheetGenerateDTO dto) {
        List<SalRule> rules = salRuleMapper.selectList(null);
        BigDecimal siRate = getRuleValue(rules, "social_insurance");
        BigDecimal taxRate = getRuleValue(rules, "tax_rate");

        // 读取启用字段的公式
        List<SalField> fields = salFieldMapper.selectList(
                new LambdaQueryWrapper<SalField>()
                        .eq(SalField::getStatus, 1)
                        .orderByAsc(SalField::getSort));

        for (Long employeeId : dto.getEmployeeIds()) {
            SalSheet sheet = salSheetMapper.selectOne(
                    new LambdaQueryWrapper<SalSheet>()
                            .eq(SalSheet::getEmployeeId, employeeId)
                            .eq(SalSheet::getMonth, dto.getMonth()));
            if (sheet == null) continue;
            if (sheet.getStatus() != 0) continue; // 只处理草稿

            HrEmployee emp = hrEmployeeMapper.selectById(employeeId);
            BigDecimal base = sheet.getBaseSalary() != null ? sheet.getBaseSalary() : BigDecimal.ZERO;
            BigDecimal perf = sheet.getPerfSalary() != null ? sheet.getPerfSalary() : BigDecimal.ZERO;
            BigDecimal subsidy = sheet.getSubsidy() != null ? sheet.getSubsidy() : BigDecimal.ZERO;
            BigDecimal ot = sheet.getOvertimePay() != null ? sheet.getOvertimePay() : BigDecimal.ZERO;
            BigDecimal totalIncome = base.add(perf).add(subsidy).add(ot);

            // 应用启用字段公式，记录字段值
            BigDecimal extraDeduction = BigDecimal.ZERO;
            Map<String, BigDecimal> fieldValues = new LinkedHashMap<>();
            for (SalField field : fields) {
                BigDecimal val = evalFormula(field.getFormula(), base, emp);
                fieldValues.put(field.getCode(), val);
                if ("income".equals(field.getType())) {
                    totalIncome = totalIncome.add(val);
                } else if ("deduction".equals(field.getType())) {
                    extraDeduction = extraDeduction.add(val);
                }
            }

            sheet.setTotalIncome(totalIncome);

            BigDecimal si = BigDecimal.ZERO;
            if (siRate.compareTo(BigDecimal.ZERO) > 0) {
                si = totalIncome.multiply(siRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            }
            sheet.setSocialInsurance(si);

            BigDecimal tax = BigDecimal.ZERO;
            if (taxRate.compareTo(BigDecimal.ZERO) > 0) {
                tax = totalIncome.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            }
            sheet.setTax(tax);

            BigDecimal totalDeduction = si.add(tax).add(extraDeduction);
            sheet.setTotalDeduction(totalDeduction);
            sheet.setNetSalary(totalIncome.subtract(totalDeduction));
            sheet.setStatus(1);

            // 序列化自定义字段值
            try {
                sheet.setFieldValues(objectMapper.writeValueAsString(fieldValues));
            } catch (Exception e) {
                log.warn("序列化自定义字段值失败: {}", e.getMessage());
            }

            // 使用 UpdateWrapper 精确更新，避免 @TableLogic 干扰
            LambdaUpdateWrapper<SalSheet> uw = new LambdaUpdateWrapper<>();
            uw.eq(SalSheet::getId, sheet.getId())
              .set(SalSheet::getTotalIncome, sheet.getTotalIncome())
              .set(SalSheet::getSocialInsurance, sheet.getSocialInsurance())
              .set(SalSheet::getTax, sheet.getTax())
              .set(SalSheet::getTotalDeduction, sheet.getTotalDeduction())
              .set(SalSheet::getNetSalary, sheet.getNetSalary())
              .set(SalSheet::getFieldValues, sheet.getFieldValues())
              .set(SalSheet::getStatus, 1);
            salSheetMapper.update(null, uw);
        }

        log.info("生成完成: month={}, 员工数={}, 应用字段公式={}个", dto.getMonth(), dto.getEmployeeIds().size(), fields.size());
    }

    // ==================== 编辑单个记录（仅允许编辑草稿） ====================

    @Override
    @Transactional
    public void update(Long id, SalSheetEditDTO dto) {
        SalSheet sheet = salSheetMapper.selectById(id);
        if (sheet == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "工资记录不存在");
        }
        // 只允许编辑草稿（status=0）
        if (sheet.getStatus() != 0) {
            throw new BusinessException(400, "仅允许编辑草稿状态的记录");
        }
        if (dto.getPerfSalary() != null) sheet.setPerfSalary(dto.getPerfSalary());
        if (dto.getSubsidy() != null) sheet.setSubsidy(dto.getSubsidy());
        if (dto.getOvertimePay() != null) sheet.setOvertimePay(dto.getOvertimePay());
        salSheetMapper.updateById(sheet);
    }

    // ==================== 清除数据 ====================

    @Override
    @Transactional
    public int deleteByStatus(Integer status) {
        LambdaQueryWrapper<SalSheet> wrapper = new LambdaQueryWrapper<>();
        if (status == 0) {
            wrapper.eq(SalSheet::getStatus, 0);
        } else {
            // status=1 时也清除已导出(status=2)的记录
            wrapper.ge(SalSheet::getStatus, 1);
        }
        int count = salSheetMapper.delete(wrapper);
        log.info("清除工资表数据: paramStatus={}, 数量={}", status, count);
        return count;
    }

    // ==================== Excel 导入 ====================

    @Override
    public int importExcel(MultipartFile file) {
        SalSheetImportResult result = new SalSheetImportResult();
        log.info("导入开始 (无事务模式，每行自动提交)");
        try {
            EasyExcel.read(file.getInputStream(), SalSheetImportRow.class,
                    new SalSheetImportListener(result, this)).sheet().doRead();
        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new BusinessException(500, "无法读取导入文件，请确认文件未损坏");
        } catch (RuntimeException e) {
            log.error("导入处理异常", e);
            throw new BusinessException(500, "导入失败: " + e.getMessage());
        }
        log.info("导入完成: 成功 {} 条, 失败 {} 条", result.created, result.failed);
        return result.created;
    }

    // ==================== 标记导出 ====================

    /**
     * 将指定月份 status=1(已生成) 的记录标记为 status=2(已导出)
     */
    @Override
    @Transactional
    public int markExported(String month) {
        List<SalSheet> sheets = salSheetMapper.selectList(
                new LambdaQueryWrapper<SalSheet>()
                        .eq(SalSheet::getMonth, month)
                        .eq(SalSheet::getStatus, 1));
        if (sheets.isEmpty()) return 0;
        sheets.forEach(s -> { s.setStatus(2); salSheetMapper.updateById(s); });
        return sheets.size();
    }

    // ==================== 私有方法 ====================

    private BigDecimal getRuleValue(List<SalRule> rules, String type) {
        return rules.stream()
                .filter(r -> type.equals(r.getType()))
                .findFirst()
                .map(SalRule::getValue)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 计算公式值。支持变量: base_salary（基本工资）、years（工龄年数）
     * 公式示例: "base_salary * 0.08", "200", "base_salary * 0.02 * years"
     */
    private BigDecimal evalFormula(String formula, BigDecimal baseSalary, HrEmployee emp) {
        if (formula == null || formula.isBlank()) return BigDecimal.ZERO;
        String expr = formula.trim();

        // 计算工龄
        long years = 0;
        if (emp != null && emp.getEntryDate() != null) {
            years = ChronoUnit.YEARS.between(emp.getEntryDate(), LocalDate.now());
        }

        // 替换变量
        expr = expr.replace("base_salary", baseSalary.toPlainString());
        expr = expr.replace("years", String.valueOf(years));

        try {
            return evaluate(expr);
        } catch (Exception e) {
            log.warn("公式计算失败: formula={}, expr={}", formula, expr, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 简单算术表达式求值，支持 + - * / 和括号，使用 BigDecimal 精确计算
     */
    private BigDecimal evaluate(String expr) {
        return parseAddSub(new ExprReader(expr.replaceAll("\\s+", "")));
    }

    private BigDecimal parseAddSub(ExprReader r) {
        BigDecimal left = parseMulDiv(r);
        while (r.hasMore()) {
            char op = r.peek();
            if (op == '+') { r.next(); left = left.add(parseMulDiv(r)); }
            else if (op == '-') { r.next(); left = left.subtract(parseMulDiv(r)); }
            else break;
        }
        return left;
    }

    private BigDecimal parseMulDiv(ExprReader r) {
        BigDecimal left = parseAtom(r);
        while (r.hasMore()) {
            char op = r.peek();
            if (op == '*') { r.next(); left = left.multiply(parseAtom(r)); }
            else if (op == '/') {
                r.next();
                BigDecimal divisor = parseAtom(r);
                if (divisor.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("除以零");
                }
                left = left.divide(divisor, 10, RoundingMode.HALF_UP);
            }
            else break;
        }
        return left;
    }

    private BigDecimal parseAtom(ExprReader r) {
        if (!r.hasMore()) throw new IllegalArgumentException("表达式不完整");
        if (r.peek() == '(') {
            r.next(); // skip (
            BigDecimal val = parseAddSub(r);
            if (!r.hasMore() || r.next() != ')') throw new IllegalArgumentException("缺少右括号");
            return val;
        }
        // 数字（含负号）
        boolean negative = false;
        if (r.peek() == '-') { negative = true; r.next(); }
        StringBuilder sb = new StringBuilder();
        if (r.hasMore() && r.peek() == '.') sb.append('0');
        while (r.hasMore() && (Character.isDigit(r.peek()) || r.peek() == '.')) {
            sb.append(r.next());
        }
        if (sb.isEmpty()) throw new IllegalArgumentException("期望数字，实际遇到: " + r.peek());
        BigDecimal val = new BigDecimal(sb.toString());
        return negative ? val.negate() : val;
    }

    /**
     * 字符流读取器，便于递归下降解析
     */
    private static class ExprReader {
        private final String s;
        private int pos;
        ExprReader(String s) { this.s = s; this.pos = 0; }
        char peek() { return s.charAt(pos); }
        char next() { return s.charAt(pos++); }
        boolean hasMore() { return pos < s.length(); }
    }

    private SalSheetVO toVO(SalSheet sheet, Map<Long, HrEmployee> empMap, Map<Long, String> deptNameMap, Map<String, SalField> fieldMap) {
        SalSheetVO vo = new SalSheetVO();
        vo.setId(sheet.getId());
        vo.setEmployeeId(sheet.getEmployeeId());
        vo.setMonth(sheet.getMonth());
        vo.setBaseSalary(sheet.getBaseSalary());
        vo.setPerfSalary(sheet.getPerfSalary());
        vo.setSubsidy(sheet.getSubsidy());
        vo.setOvertimePay(sheet.getOvertimePay());
        vo.setTotalIncome(sheet.getTotalIncome());
        vo.setSocialInsurance(sheet.getSocialInsurance());
        vo.setTax(sheet.getTax());
        vo.setTotalDeduction(sheet.getTotalDeduction());
        vo.setNetSalary(sheet.getNetSalary());
        vo.setStatus(sheet.getStatus());

        HrEmployee emp = empMap != null ? empMap.get(sheet.getEmployeeId()) : null;
        if (emp != null) {
            vo.setEmpNo(emp.getEmpNo());
            vo.setEmployeeName(emp.getName());
            String deptName = deptNameMap != null ? deptNameMap.get(emp.getDeptId()) : null;
            vo.setDeptName(deptName != null ? deptName : "");
        }

        // 解析自定义字段值
        if (sheet.getFieldValues() != null && !sheet.getFieldValues().isBlank() && fieldMap != null) {
            try {
                Map<String, BigDecimal> values = objectMapper.readValue(sheet.getFieldValues(),
                        new TypeReference<Map<String, BigDecimal>>() {});
                List<Map<String, Object>> customFields = new ArrayList<>();
                for (SalField field : fieldMap.values()) {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", field.getName());
                    item.put("code", field.getCode());
                    item.put("type", field.getType());
                    item.put("value", values.getOrDefault(field.getCode(), BigDecimal.ZERO));
                    customFields.add(item);
                }
                vo.setCustomFields(customFields);
            } catch (Exception e) {
                log.warn("解析自定义字段值失败: {}", e.getMessage());
            }
        }
        return vo;
    }

    // ==================== 导入行定义 + 监听器 ====================

    @Data
    public static class SalSheetImportRow {
        @ExcelProperty("工号")
        private String empNo;
        @ExcelProperty("姓名")
        private String employeeName;
        @ExcelProperty("月份")
        private String month;
        @ExcelProperty("绩效工资")
        private String perfSalary;
        @ExcelProperty("补贴")
        private String subsidy;
        @ExcelProperty("加班费")
        private String overtimePay;
    }

    @Data
    public static class SalSheetImportResult {
        private int created;
        private int updated;
        private int failed;
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class SalSheetImportListener extends AnalysisEventListener<SalSheetImportRow> {

        private final SalSheetImportResult result;
        private final SalSheetServiceImpl service;

        @Override
        public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
            log.info("导入表头: {}", headMap.values());
            // 检查必要列是否存在（按名称匹配，不依赖列顺序）
            List<String> headers = headMap.values().stream().map(String::trim).collect(Collectors.toList());
            if (!headers.contains("工号")) {
                throw new BusinessException(500, "Excel缺少「工号」列，当前表头: " + headers);
            }
            if (!headers.contains("姓名")) {
                throw new BusinessException(500, "Excel缺少「姓名」列，当前表头: " + headers);
            }
            if (!headers.contains("月份")) {
                throw new BusinessException(500, "Excel缺少「月份」列，当前表头: " + headers);
            }
        }

        @Override
        public void invoke(SalSheetImportRow row, AnalysisContext context) {
            int rowIndex = context.readRowHolder() != null ? context.readRowHolder().getRowIndex() + 1 : 0;
            // 跳过空行（工号、姓名、月份都为空）
            if ((row.getEmpNo() == null || row.getEmpNo().isBlank())
                    && (row.getEmployeeName() == null || row.getEmployeeName().isBlank())
                    && (row.getMonth() == null || row.getMonth().isBlank())) return;
            try {
                // trim 去除 Excel 单元格中可能的空格和不可见字符
                String empNo = row.getEmpNo() != null ? row.getEmpNo().trim() : null;
                String employeeName = row.getEmployeeName() != null ? row.getEmployeeName().trim() : null;
                String month = row.getMonth() != null ? row.getMonth().trim() : null;

                if (empNo == null || empNo.isBlank()) {
                    log.warn("导入第 {} 行: 工号为空", rowIndex);
                    result.failed++;
                    return;
                }
                if (employeeName == null || employeeName.isBlank()) {
                    log.warn("导入第 {} 行: 姓名为空", rowIndex);
                    result.failed++;
                    return;
                }
                if (month == null || month.isBlank()) {
                    log.warn("导入第 {} 行: 月份为空", rowIndex);
                    result.failed++;
                    return;
                }
                // 规范化月份格式为 YYYY-MM（兼容 2026-7、202607、2026/07 等）
                month = normalizeMonth(month);
                log.info("导入第 {} 行: 工号={}, 姓名={}, 月份={}", rowIndex, empNo, employeeName, month);

                HrEmployee emp = service.hrEmployeeMapper.selectOne(
                        new LambdaQueryWrapper<HrEmployee>()
                                .eq(HrEmployee::getEmpNo, empNo));
                if (emp == null) {
                    log.warn("导入第 {} 行: 工号「{}」(长度={})在花名册中不存在", rowIndex, empNo, empNo.length());
                    result.failed++;
                    return;
                }

                // 校验姓名是否与花名册一致
                String empName = emp.getName() != null ? emp.getName().trim() : "";
                if (!employeeName.equals(empName)) {
                    log.warn("导入第 {} 行: 姓名「{}」与花名册「{}」不一致", rowIndex, employeeName, empName);
                    result.failed++;
                    return;
                }

                // 安全解析数值字段
                BigDecimal perfSalary = parseDecimal(row.getPerfSalary());
                BigDecimal subsidy = parseDecimal(row.getSubsidy());
                BigDecimal overtimePay = parseDecimal(row.getOvertimePay());

                // 使用 XML mapper 原生 SQL，完全绕过 MyBatis Plus @TableLogic 处理
                BigDecimal empBase = emp.getBaseSalary() != null ? emp.getBaseSalary() : BigDecimal.ZERO;

                SalSheet sheet = new SalSheet();
                sheet.setEmployeeId(emp.getId());
                sheet.setMonth(month);
                sheet.setBaseSalary(empBase);
                sheet.setPerfSalary(perfSalary);
                sheet.setSubsidy(subsidy);
                sheet.setOvertimePay(overtimePay);
                sheet.setTotalIncome(BigDecimal.ZERO);
                sheet.setSocialInsurance(BigDecimal.ZERO);
                sheet.setTax(BigDecimal.ZERO);
                sheet.setTotalDeduction(BigDecimal.ZERO);
                sheet.setNetSalary(BigDecimal.ZERO);
                sheet.setStatus(0);
                sheet.setDeleted(0);

                // 先尝试 UPDATE（匹配含软删除的记录，并恢复 deleted=0）
                int updated = service.salSheetMapper.updateRawByEmpMonth(sheet);
                if (updated == 0) {
                    // 无匹配记录，INSERT 新建
                    service.salSheetMapper.insertRaw(sheet);
                }
                log.info("导入第 {} 行: {} (employeeId={}, month={})",
                        rowIndex, updated > 0 ? "更新" : "新建", emp.getId(), month);
                result.created++;
            } catch (Exception e) {
                log.error("导入第 {} 行失败: {}", rowIndex, e.getMessage());
                result.failed++;
            }
        }

        private BigDecimal parseDecimal(String val) {
            if (val == null || val.isBlank()) return BigDecimal.ZERO;
            try {
                return new BigDecimal(val.trim().replace(",", "").replace("，", ""));
            } catch (NumberFormatException e) {
                log.warn("数值解析失败: {}", val);
                return BigDecimal.ZERO;
            }
        }

        /**
         * 规范化月份格式为 YYYY-MM
         * 兼容：2026-7 → 2026-07、202607 → 2026-07、2026/07 → 2026-07
         */
        private String normalizeMonth(String raw) {
            // 去除非数字字符，得到纯数字
            String digits = raw.replaceAll("[^0-9]", "");
            if (digits.length() == 4) {
                // "2026" → "2026-当前月"
                return digits + "-" + String.format("%02d", java.time.LocalDate.now().getMonthValue());
            }
            if (digits.length() == 6) {
                // "202607" → "2026-07"
                return digits.substring(0, 4) + "-" + digits.substring(4, 6);
            }
            if (digits.length() >= 7) {
                // "2026-7" stripped to "20267" → treat as year(4)+mon(rest)
                // 或者 "2026-07" stripped to "202607"
                return digits.substring(0, 4) + "-" + String.format("%02d", Integer.parseInt(digits.substring(4)));
            }
            // 其它情况，尝试保留原格式
            log.warn("无法规范化月份格式: {}, 保留原始值", raw);
            return raw;
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("导入解析完成: 新建 {} 条, 更新 {} 条, 失败 {} 条", result.created, result.updated, result.failed);
        }

        @Override
        public void onException(Exception exception, AnalysisContext context) {
            log.error("导入解析异常, row={}", context.readRowHolder() != null ? context.readRowHolder().getRowIndex() + 1 : "?", exception);
            result.failed++;
            // 继续解析下一行，不中断
        }
    }
}
