package com.hr.module.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("att_oa_flow")
public class AttOaFlow extends BaseEntity {
    private Long employeeId;
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal duration;
    private String status;
}
