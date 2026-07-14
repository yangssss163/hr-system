package com.hr.module.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleDTO {
    private String name;
    private String code;
    private String description;
    private Integer status;
}
