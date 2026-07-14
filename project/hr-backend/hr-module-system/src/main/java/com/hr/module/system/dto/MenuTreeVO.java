package com.hr.module.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class MenuTreeVO extends TreeVO {
    private Integer type;
    private String path;
    private String component;
    private String permission;
    private String icon;
    private Integer visible;

    public MenuTreeVO() {
        this.setChildren(new ArrayList<>());
    }
}
