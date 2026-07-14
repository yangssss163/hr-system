package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysTaskQuery {
    private String keyword;
    private String priority;
    private String status;
    private Long assigneeId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
