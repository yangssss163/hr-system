package com.hr.module.salary.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_adjust")
public class SalAdjust extends BaseEntity {
    private Long employeeId;
    private BigDecimal beforeSalary;
    private BigDecimal afterSalary;
    private BigDecimal adjustAmount;
    private String adjustType;
    private LocalDate effectiveDate;
    private String remark;
}
