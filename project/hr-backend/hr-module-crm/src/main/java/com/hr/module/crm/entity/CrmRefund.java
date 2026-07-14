package com.hr.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_refund")
public class CrmRefund extends BaseEntity {
    private Long orderId;
    private Long customerId;
    private String refundNo;
    private BigDecimal amount;
    private LocalDate refundDate;
    private String reason;
    private String status;
    private Long ownerId;
}
