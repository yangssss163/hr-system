package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysScheduleQuery {
    private String keyword;
    private Long userId;
    private String startTime;
    private String endTime;
    private Integer page = 1;
    private Integer pageSize = 10;
}
