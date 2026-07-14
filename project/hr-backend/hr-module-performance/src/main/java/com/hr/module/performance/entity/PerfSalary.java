package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_salary")
public class PerfSalary extends BaseEntity {
    private Long levelId;
    private String salaryRange;
    private Integer sort;
}
