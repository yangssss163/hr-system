package com.hr.module.employee.dto;

import lombok.Data;

@Data
public class TransferQuery {
    private Long employeeId;
    private String transferType;
    private String startDate;
    private String endDate;
    private Integer page = 1;
    private Integer pageSize = 10;
}
