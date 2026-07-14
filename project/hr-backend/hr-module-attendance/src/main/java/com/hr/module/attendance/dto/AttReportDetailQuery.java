package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttReportDetailQuery {
    private Long deptId;
    private String dateStart;
    private String dateEnd;
    private Long employeeId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
