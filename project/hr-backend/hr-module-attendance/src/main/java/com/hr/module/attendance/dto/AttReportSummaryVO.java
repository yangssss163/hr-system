package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttReportSummaryVO {
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private Integer shouldWorkDays;
    private Integer actualWorkDays;
    private Integer lateCount;
    private Integer earlyCount;
    private Integer absentCount;
    private Integer leaveCount;
    private Double overtimeHours;
}
