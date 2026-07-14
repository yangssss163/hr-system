package com.hr.module.salary.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_rule")
public class SalRule extends BaseEntity {
    private String type;
    private BigDecimal value;
    private String unit;
    private String remark;
}
