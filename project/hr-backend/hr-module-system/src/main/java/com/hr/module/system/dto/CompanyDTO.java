package com.hr.module.system.dto;

import lombok.Data;

@Data
public class CompanyDTO {
    private String name;
    private String code;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
