package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysScheduleVO {
    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private String content;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer allDay;
    private String color;
    private LocalDateTime createTime;
}
