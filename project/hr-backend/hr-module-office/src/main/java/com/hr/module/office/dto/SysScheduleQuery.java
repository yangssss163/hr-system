package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysScheduleQuery {
    private String keyword;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page = 1;
    private Integer pageSize = 10;
}
