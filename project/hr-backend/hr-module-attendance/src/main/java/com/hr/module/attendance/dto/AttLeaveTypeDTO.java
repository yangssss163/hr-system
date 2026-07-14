package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AttLeaveTypeDTO {
    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "编码不能为空")
    private String code;

    private BigDecimal defaultDays;
    private Integer enabled;
}
