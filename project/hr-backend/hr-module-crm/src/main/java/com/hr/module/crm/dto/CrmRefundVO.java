package com.hr.module.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrmRefundVO {
    private Long id;
    private Long orderId;
    private Long customerId;
    private String customerName;
    private String refundNo;
    private BigDecimal amount;
    private String refundDate;
    private String reason;
    private String status;
    private Long ownerId;
    private String ownerName;
    private LocalDateTime createTime;
}
