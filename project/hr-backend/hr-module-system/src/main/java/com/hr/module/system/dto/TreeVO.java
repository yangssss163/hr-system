package com.hr.module.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class TreeVO {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status;
    private List<? extends TreeVO> children;
}
