package com.hr.module.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlowExpenseDTO {
    @NotNull(message = "申请人不能为空")
    private Long applicantId;
    @NotNull(message = "报销金额不能为空")
    private BigDecimal amount;
    @NotBlank(message = "报销日期不能为空")
    private String expenseDate;
    @NotBlank(message = "报销类别不能为空")
    private String category;
    private String description;
}
