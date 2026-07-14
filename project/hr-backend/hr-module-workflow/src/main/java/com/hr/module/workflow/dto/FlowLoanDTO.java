package com.hr.module.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FlowLoanDTO {
    @NotNull(message = "申请人不能为空")
    private Long applicantId;
    @NotNull(message = "借款金额不能为空")
    private BigDecimal amount;
    @NotBlank(message = "借款日期不能为空")
    private String loanDate;
    private String reason;
    private String repaymentDate;
}
