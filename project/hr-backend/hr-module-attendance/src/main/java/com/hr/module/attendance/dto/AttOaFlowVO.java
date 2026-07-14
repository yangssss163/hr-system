package com.hr.module.attendance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AttOaFlowVO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String type;
    private String typeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal duration;
    private String status;
    private String statusName;
    private LocalDateTime createTime;
}
