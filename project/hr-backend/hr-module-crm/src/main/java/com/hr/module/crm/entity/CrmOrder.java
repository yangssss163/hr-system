package com.hr.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_order")
public class CrmOrder extends BaseEntity {
    private Long opportunityId;
    private Long customerId;
    private String orderNo;
    private BigDecimal amount;
    private String status;
    private LocalDate signDate;
    private Long ownerId;
    private String remark;
}
