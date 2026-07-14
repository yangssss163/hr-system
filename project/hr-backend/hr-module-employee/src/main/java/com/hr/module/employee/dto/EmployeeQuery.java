package com.hr.module.employee.dto;

import lombok.Data;

@Data
public class EmployeeQuery {
    private String keyword;
    private Long deptId;
    private Long positionId;
    private Long companyId;
    private Integer status;
    private String entryDateStart;
    private String entryDateEnd;
    private Integer page = 1;
    private Integer pageSize = 10;
}
