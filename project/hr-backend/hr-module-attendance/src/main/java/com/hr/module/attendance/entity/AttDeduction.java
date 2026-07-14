package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_deduction")
public class AttDeduction extends BaseEntity {
    private String type;
    private BigDecimal deduction;
    private String unit;
    private String remark;
}
