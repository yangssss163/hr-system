package com.hr.module.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PerfLevelDTO {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal scoreMin;

    @NotNull
    private BigDecimal scoreMax;

    @NotNull
    private BigDecimal coefficient;

    private Integer sort;
}
