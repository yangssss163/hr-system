package com.hr.module.crm.dto;

import lombok.Data;

@Data
public class CrmExpenseQuery {
    private String keyword;
    private String status;
    private String category;
    private Long applicantId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
