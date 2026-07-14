package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrmExpenseDTO {
    @NotBlank(message = "费用名称不能为空")
    private String name;

    private BigDecimal amount;
    private String expenseDate;
    private String category;
    private String status;
    private Long applicantId;
    private String remark;
}
