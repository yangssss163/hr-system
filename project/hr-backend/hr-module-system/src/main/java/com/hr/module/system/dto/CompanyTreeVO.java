package com.hr.module.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompanyTreeVO extends TreeVO {
    private String code;

    public CompanyTreeVO() {
        this.setChildren(new ArrayList<>());
    }
}
