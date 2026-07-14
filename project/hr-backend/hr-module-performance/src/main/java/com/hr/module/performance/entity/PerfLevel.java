package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("perf_level")
public class PerfLevel extends BaseEntity {
    private String name;
    private BigDecimal scoreMin;
    private BigDecimal scoreMax;
    private BigDecimal coefficient;
    private Integer sort;
}
