package com.hr.module.crm.dto;

import lombok.Data;

@Data
public class CrmPaymentQuery {
    private String keyword;
    private String status;
    private Long orderId;
    private Long customerId;
    private Long ownerId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
