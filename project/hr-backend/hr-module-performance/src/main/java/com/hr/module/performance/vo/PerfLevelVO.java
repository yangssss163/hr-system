package com.hr.module.performance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerfLevelVO {
    private Long id;
    private String name;
    private BigDecimal scoreMin;
    private BigDecimal scoreMax;
    private BigDecimal coefficient;
    private Integer sort;
}
