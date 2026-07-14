package com.hr.module.salary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalRuleEditDTO {
    @NotNull(message = "值不能为空")
    private BigDecimal value;

    private String remark;
}
