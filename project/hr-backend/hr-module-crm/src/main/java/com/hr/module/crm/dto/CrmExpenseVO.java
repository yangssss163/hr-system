package com.hr.module.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrmExpenseVO {
    private Long id;
    private String name;
    private BigDecimal amount;
    private String expenseDate;
    private String category;
    private String status;
    private Long applicantId;
    private String applicantName;
    private String remark;
    private LocalDateTime createTime;
}
