package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_card_rule")
public class AttCardRule extends BaseEntity {
    private String name;
    private Integer minCardCount;
    private Integer allowOvertime;
    private Integer status;
}
