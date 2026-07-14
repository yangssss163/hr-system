package com.hr.module.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeptTreeVO extends TreeVO {
    private Long companyId;
    private String companyName;

    public DeptTreeVO() {
        this.setChildren(new ArrayList<>());
    }
}
