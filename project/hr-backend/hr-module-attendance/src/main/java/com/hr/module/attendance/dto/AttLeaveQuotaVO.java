package com.hr.module.attendance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttLeaveQuotaVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String leaveType;
    private String leaveTypeName;
    private Integer year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private BigDecimal remainDays;
}
