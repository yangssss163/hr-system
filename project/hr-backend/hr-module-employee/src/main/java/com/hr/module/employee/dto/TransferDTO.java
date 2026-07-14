package com.hr.module.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransferDTO {
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotBlank(message = "异动类型不能为空")
    private String transferType;

    private Long afterDeptId;
    private Long afterPositionId;

    @NotBlank(message = "生效日期不能为空")
    private String effectiveDate;

    private String reason;
}
