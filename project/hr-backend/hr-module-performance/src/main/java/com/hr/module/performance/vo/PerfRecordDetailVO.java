package com.hr.module.performance.vo;

import lombok.Data;

import java.util.List;

@Data
public class PerfRecordDetailVO {
    private Long id;
    private Long planId;
    private String planName;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String deptName;
    private java.math.BigDecimal totalScore;
    private Long levelId;
    private String levelName;
    private String evaluatorName;
    private java.time.LocalDateTime evaluateTime;
    private String evaluation;
    private List<PerfRecordItemVO> items;
}
