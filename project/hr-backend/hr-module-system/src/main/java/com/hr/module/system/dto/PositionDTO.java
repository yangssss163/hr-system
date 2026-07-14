package com.hr.module.system.dto;

import lombok.Data;

@Data
public class PositionDTO {
    private String name;
    private Long deptId;
    private Integer sort;
    private Integer status;
}
