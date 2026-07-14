package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_leave_type")
public class AttLeaveType extends BaseEntity {
    private String name;
    private String code;
    private BigDecimal defaultDays;
    private Integer enabled;
}
