package com.hr.module.system.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleVO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Integer status;
    private List<Long> menuIds;
    private LocalDateTime createTime;
}
