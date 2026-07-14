package com.hr.module.performance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PerfSalaryDTO {
    @NotNull
    private Long levelId;

    private String salaryRange;

    private Integer sort;
}
