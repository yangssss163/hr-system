package com.hr.module.performance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PerfRecordDTO {
    @NotNull
    private Long planId;

    @NotNull
    private Long employeeId;

    private Long evaluatorId;

    private List<PerfRecordItemDTO> items;

    private BigDecimal totalScore;

    private String evaluation;

    private Long levelId;
}
