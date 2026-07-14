package com.hr.module.salary.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SalAdjustVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private BigDecimal beforeSalary;
    private BigDecimal afterSalary;
    private BigDecimal adjustAmount;
    private String adjustType;
    private String adjustTypeName;
    private String effectiveDate;
    private String remark;
    private LocalDateTime createTime;
}
