package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("perf_plan_employee")
public class PerfPlanEmployee implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long planId;
    private Long employeeId;
}
