package com.hr.module.office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysScheduleDTO {
    @NotNull(message = "用户不能为空")
    private Long userId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    private Integer allDay;
    private String color;
}
