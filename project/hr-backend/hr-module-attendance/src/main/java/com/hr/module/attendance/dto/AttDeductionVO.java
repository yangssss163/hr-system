package com.hr.module.attendance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttDeductionVO {
    private Long id;
    private String type;
    private String typeName;
    private BigDecimal deduction;
    private String unit;
    private String remark;
}
