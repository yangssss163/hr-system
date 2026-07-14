package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttDeductionEditDTO {
    @NotNull(message = "扣款金额不能为空")
    private BigDecimal deduction;

    private String unit;
    private String remark;
}
