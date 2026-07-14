package com.hr.module.salary.dto;

import lombok.Data;

@Data
public class SalFieldVO {
    private Long id;
    private String name;
    private String code;
    private String type;
    private String typeName;
    private String formula;
    private Integer sort;
    private Integer status;

    public String getTypeName() {
        if ("income".equals(type)) {
            return "应发";
        }
        if ("deduction".equals(type)) {
            return "应扣";
        }
        return typeName;
    }
}
