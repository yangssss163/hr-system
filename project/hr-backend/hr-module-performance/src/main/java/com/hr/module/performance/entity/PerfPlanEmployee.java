package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 考核计划-员工关联表（复合主键：plan_id + employee_id）
 */
@Data
@TableName("perf_plan_employee")
public class PerfPlanEmployee implements Serializable {

    private Long planId;
    private Long employeeId;
}
