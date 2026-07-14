package com.hr.module.system.dto;

import lombok.Data;

@Data
public class UserQuery {
    private String keyword;
    private Long deptId;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
