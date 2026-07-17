package com.hr.module.salary.dto;

import lombok.Data;

@Data
public class SalSheetQuery {
    private String month;
    private Long deptId;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
