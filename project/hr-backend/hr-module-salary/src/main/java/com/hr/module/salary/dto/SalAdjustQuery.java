package com.hr.module.salary.dto;

import lombok.Data;

@Data
public class SalAdjustQuery {
    private Long employeeId;
    private String keyword;
    private Integer page = 1;
    private Integer pageSize = 10;
}
