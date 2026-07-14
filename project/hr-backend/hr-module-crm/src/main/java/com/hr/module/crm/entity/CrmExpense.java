package com.hr.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_expense")
public class CrmExpense extends BaseEntity {
    private String name;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String category;
    private String status;
    private Long applicantId;
    private String remark;
}
