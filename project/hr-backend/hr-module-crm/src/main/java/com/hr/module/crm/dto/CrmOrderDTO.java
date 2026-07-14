package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrmOrderDTO {
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    private Long opportunityId;
    private Long customerId;
    private BigDecimal amount;
    private String status;
    private String signDate;
    private Long ownerId;
    private String remark;
}
