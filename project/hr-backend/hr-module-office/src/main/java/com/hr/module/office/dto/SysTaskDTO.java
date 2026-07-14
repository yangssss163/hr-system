package com.hr.module.office.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SysTaskDTO {
    private Long creatorId;
    private Long assigneeId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    private String priority;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    private LocalDateTime completeTime;
}
