package com.hr.module.salary.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_sheet")
public class SalSheet extends BaseEntity {
    private Long employeeId;
    private String month;
    private BigDecimal baseSalary;
    private BigDecimal perfSalary;
    private BigDecimal subsidy;
    private BigDecimal overtimePay;
    private BigDecimal totalIncome;
    private BigDecimal socialInsurance;
    private BigDecimal tax;
    private BigDecimal totalDeduction;
    private BigDecimal netSalary;
    private Integer status;
}
