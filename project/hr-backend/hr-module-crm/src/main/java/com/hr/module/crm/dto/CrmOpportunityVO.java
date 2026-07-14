package com.hr.module.crm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrmOpportunityVO {
    private Long id;
    private String name;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private String stage;
    private Integer probability;
    private String expectedCloseDate;
    private Long ownerId;
    private String ownerName;
    private String contactName;
    private String contactPhone;
    private String remark;
    private LocalDateTime createTime;
}
