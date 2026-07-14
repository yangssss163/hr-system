package com.hr.module.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalAdjustDTO {
    @NotNull(message = "员工不能为空")
    private Long employeeId;

    @NotNull(message = "调后薪资不能为空")
    private BigDecimal afterSalary;

    @NotBlank(message = "调薪类型不能为空")
    private String adjustType;

    @NotBlank(message = "生效日期不能为空")
    private String effectiveDate;

    private String remark;
}
