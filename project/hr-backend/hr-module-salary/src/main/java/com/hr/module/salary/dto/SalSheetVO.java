package com.hr.module.salary.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalSheetVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private BigDecimal baseSalary;
    private BigDecimal perfSalary;
    private BigDecimal subsidy;
    private BigDecimal overtimePay;
    private BigDecimal totalIncome;
    private BigDecimal socialInsurance;
    private BigDecimal tax;
    private BigDecimal totalDeduction;
    private BigDecimal netSalary;
}
