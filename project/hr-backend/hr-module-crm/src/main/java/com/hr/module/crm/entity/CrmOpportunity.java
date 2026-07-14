package com.hr.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_opportunity")
public class CrmOpportunity extends BaseEntity {
    private String name;
    private Long customerId;
    private BigDecimal amount;
    private String stage;
    private Integer probability;
    private LocalDate expectedCloseDate;
    private Long ownerId;
    private String contactName;
    private String contactPhone;
    private String remark;
}
