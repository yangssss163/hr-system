package com.hr.module.system.dto;

import lombok.Data;

@Data
public class PositionVO {
    private Long id;
    private String name;
    private Long deptId;
    private String deptName;
    private Integer sort;
    private Integer status;
}
