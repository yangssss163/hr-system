package com.hr.module.system.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private Long parentId;
    private String name;
    private Integer type;
    private String path;
    private String component;
    private String permission;
    private String icon;
    private Integer sort;
    private Integer visible;
}
