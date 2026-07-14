package com.hr.module.performance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerfRecordItemVO {
    private Long id;
    private String indicator;
    private Integer weight;
    private BigDecimal score;
}
