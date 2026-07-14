package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrmRefundDTO {
    @NotBlank(message = "退款编号不能为空")
    private String refundNo;

    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String refundDate;
    private String reason;
    private String status;
    private Long ownerId;
}
