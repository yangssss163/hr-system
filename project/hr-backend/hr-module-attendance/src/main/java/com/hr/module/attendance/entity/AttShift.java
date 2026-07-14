package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_shift")
public class AttShift extends BaseEntity {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer lateBuffer;
    private Integer earlyBuffer;
    private Integer status;
}
