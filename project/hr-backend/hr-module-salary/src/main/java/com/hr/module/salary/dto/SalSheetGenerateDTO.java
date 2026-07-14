package com.hr.module.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SalSheetGenerateDTO {
    @NotBlank(message = "月份不能为空")
    private String month;

    @NotNull(message = "员工列表不能为空")
    private List<Long> employeeIds;
}
