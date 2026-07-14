package com.hr.module.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PerfPlanDTO {
    @NotBlank
    private String name;

    @NotNull
    private Long deptId;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    private List<Long> employeeIds;

    @NotNull
    private Integer status;
}
