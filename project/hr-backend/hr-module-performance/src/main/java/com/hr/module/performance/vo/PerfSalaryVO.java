package com.hr.module.performance.vo;

import lombok.Data;

@Data
public class PerfSalaryVO {
    private Long id;
    private Long levelId;
    private String levelName;
    private String salaryRange;
    private Integer sort;
}
