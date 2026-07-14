package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_record")
public class AttRecord extends BaseEntity {
    private Long employeeId;
    private LocalDate recordDate;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;
    private String source;
}
