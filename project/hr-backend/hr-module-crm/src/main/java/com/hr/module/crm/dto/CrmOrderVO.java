package com.hr.module.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrmOrderVO {
    private Long id;
    private Long opportunityId;
    private Long customerId;
    private String customerName;
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private String signDate;
    private Long ownerId;
    private String ownerName;
    private String remark;
    private LocalDateTime createTime;
}
