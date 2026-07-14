package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_plan")
public class PerfPlan extends BaseEntity {
    private String name;
    private Long deptId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer status;
}
