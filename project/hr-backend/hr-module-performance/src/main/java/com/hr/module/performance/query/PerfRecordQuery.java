package com.hr.module.performance.query;

import lombok.Data;

@Data
public class PerfRecordQuery {
    private Long planId;
    private Long employeeId;
    private Long deptId;
    private Long levelId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
