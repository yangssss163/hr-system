package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttLeaveQuotaAdjustDTO {
    @NotNull(message = "总天数不能为空")
    private BigDecimal totalDays;
}
