package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class SysTaskVO {
    private Long id;
    private Long creatorId;
    private Long assigneeId;
    private String title;
    private String content;
    private String priority;
    private String priorityName;
    private String status;
    private String statusName;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completeTime;
    private LocalDateTime createTime;
}
