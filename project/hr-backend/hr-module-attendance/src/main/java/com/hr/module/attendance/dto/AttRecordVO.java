package com.hr.module.attendance.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttRecordVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private LocalDate recordDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;
    private String source;
}
