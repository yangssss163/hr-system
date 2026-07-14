package com.hr.module.performance.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PerfPlanVO {
    private Long id;
    private String name;
    private Long deptId;
    private String deptName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
    private String statusName;
    private java.time.LocalDateTime createTime;
}
