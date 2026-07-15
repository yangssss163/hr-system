package com.hr.module.performance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PerfRecordVO {
    private Long id;
    private Long planId;
    private String planName;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private BigDecimal totalScore;
    private Long levelId;
    private String levelName;
    private String evaluatorName;
    private LocalDateTime evaluateTime;
    private Integer rank;
}
