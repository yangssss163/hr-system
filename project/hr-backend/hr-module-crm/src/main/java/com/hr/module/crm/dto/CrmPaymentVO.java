package com.hr.module.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrmPaymentVO {
    private Long id;
    private Long orderId;
    private Long customerId;
    private String customerName;
    private String paymentNo;
    private BigDecimal amount;
    private String paymentDate;
    private String paymentMethod;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String remark;
    private LocalDateTime createTime;
}
