package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_record")
public class PerfRecord extends BaseEntity {
    private Long employeeId;
    private Long planId;
    private Long evaluatorId;
    private BigDecimal totalScore;
    private Long levelId;
    private String evaluation;
    private LocalDateTime evaluateTime;
    private Integer status;
}
