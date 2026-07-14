package com.hr.module.salary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalSheetSyncDTO {
    @NotBlank(message = "月份不能为空")
    private String month;

    private Long deptId;
}
