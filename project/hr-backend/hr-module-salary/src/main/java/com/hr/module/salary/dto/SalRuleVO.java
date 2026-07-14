package com.hr.module.salary.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalRuleVO {
    private Long id;
    private String type;
    private String typeName;
    private BigDecimal value;
    private String unit;
    private String remark;
}
