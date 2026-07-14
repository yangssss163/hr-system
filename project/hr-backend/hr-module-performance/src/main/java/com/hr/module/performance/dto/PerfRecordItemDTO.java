package com.hr.module.performance.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerfRecordItemDTO {
    private String indicator;
    private Integer weight;
    private BigDecimal score;
}
