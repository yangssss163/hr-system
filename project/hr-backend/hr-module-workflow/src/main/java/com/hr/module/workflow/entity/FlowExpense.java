package com.hr.module.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_expense")
public class FlowExpense extends BaseEntity {
    private Long applicantId;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String category;
    private String description;
    private String status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveComment;
}
