package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttLeaveQuotaQuery {
    private Long employeeId;
    private Integer year;
    private Integer page = 1;
    private Integer pageSize = 10;
}
