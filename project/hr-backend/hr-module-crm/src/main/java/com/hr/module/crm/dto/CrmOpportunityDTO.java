package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CrmOpportunityDTO {
    @NotBlank(message = "商机名称不能为空")
    private String name;

    private Long customerId;
    private BigDecimal amount;
    private String stage;
    private Integer probability;
    private String expectedCloseDate;
    private Long ownerId;
    private String contactName;
    private String contactPhone;
    private String remark;
}
