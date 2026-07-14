package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttShiftDTO {
    @NotBlank(message = "班次名称不能为空")
    private String name;

    @NotNull(message = "上班时间不能为空")
    private String startTime;

    @NotNull(message = "下班时间不能为空")
    private String endTime;

    private Integer lateBuffer;
    private Integer earlyBuffer;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
