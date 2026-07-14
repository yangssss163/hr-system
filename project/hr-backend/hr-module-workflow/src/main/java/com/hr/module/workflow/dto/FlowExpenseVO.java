package com.hr.module.workflow.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlowExpenseVO {
    private Long id;
    private Long applicantId;
    private String applicantName;
    private BigDecimal amount;
    private String expenseDate;
    private String category;
    private String description;
    private String status;
    private String statusName;
    private Long approverId;
    private String approverName;
    private LocalDateTime createTime;
}
