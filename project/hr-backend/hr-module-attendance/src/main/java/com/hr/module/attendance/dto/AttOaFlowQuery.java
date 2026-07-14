package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttOaFlowQuery {
    private Long employeeId;
    private String type;
    private Integer page = 1;
    private Integer pageSize = 10;
}
