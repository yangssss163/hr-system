package com.hr.module.attendance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttExceptionVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private LocalDate recordDate;
    private String type;
    private String typeName;
    private String detail;
    private String oaStatus;
}
