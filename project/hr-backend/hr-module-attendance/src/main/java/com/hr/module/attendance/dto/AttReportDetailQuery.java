package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttReportDetailQuery {
    private Long deptId;
    private Long employeeId;
    private String keyword;
    private String dateStart;
    private String dateEnd;
    private Integer page = 1;
    private Integer pageSize = 10;
}
