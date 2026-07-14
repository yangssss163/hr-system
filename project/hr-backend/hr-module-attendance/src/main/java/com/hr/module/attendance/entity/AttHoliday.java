package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_holiday")
public class AttHoliday extends BaseEntity {
    private Integer year;
    private String name;
    private LocalDate date;
    private Integer days;
}
