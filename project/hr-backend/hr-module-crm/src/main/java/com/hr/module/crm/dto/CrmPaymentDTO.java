package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrmPaymentDTO {
    @NotBlank(message = "付款编号不能为空")
    private String paymentNo;

    private Long orderId;
    private Long customerId;
    private BigDecimal amount;
    private String paymentDate;
    private String paymentMethod;
    private String status;
    private Long ownerId;
    private String remark;
}
