package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttHolidayDTO {
    @NotNull(message = "年份不能为空")
    private Integer year;

    @NotBlank(message = "假期名称不能为空")
    private String name;

    @NotBlank(message = "假期日期不能为空")
    private String date;

    @NotNull(message = "天数不能为空")
    private Integer days;
}
