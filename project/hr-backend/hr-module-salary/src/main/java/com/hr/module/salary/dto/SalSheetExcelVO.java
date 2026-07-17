package com.hr.module.salary.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalSheetExcelVO {

    @ExcelProperty("月份")
    @ColumnWidth(12)
    private String month;

    @ExcelProperty("工号")
    @ColumnWidth(15)
    private String empNo;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String employeeName;

    @ExcelProperty("部门")
    @ColumnWidth(18)
    private String deptName;

    @ExcelProperty("基本工资")
    @ColumnWidth(14)
    private BigDecimal baseSalary;

    @ExcelProperty("绩效工资")
    @ColumnWidth(14)
    private BigDecimal perfSalary;

    @ExcelProperty("补贴")
    @ColumnWidth(12)
    private BigDecimal subsidy;

    @ExcelProperty("加班费")
    @ColumnWidth(12)
    private BigDecimal overtimePay;

    @ExcelProperty("应发合计")
    @ColumnWidth(14)
    private BigDecimal totalIncome;

    @ExcelProperty("社保扣除")
    @ColumnWidth(14)
    private BigDecimal socialInsurance;

    @ExcelProperty("个税")
    @ColumnWidth(12)
    private BigDecimal tax;

    @ExcelProperty("应扣合计")
    @ColumnWidth(14)
    private BigDecimal totalDeduction;

    @ExcelProperty("实发工资")
    @ColumnWidth(14)
    private BigDecimal netSalary;

    @ExcelIgnore
    private Long employeeId;
}
