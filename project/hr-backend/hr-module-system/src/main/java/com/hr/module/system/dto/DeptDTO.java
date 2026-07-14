package com.hr.module.system.dto;

import lombok.Data;

@Data
public class DeptDTO {
    private Long companyId;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
