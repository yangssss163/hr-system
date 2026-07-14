package com.hr.module.crm.dto;

import lombok.Data;

@Data
public class CrmOpportunityQuery {
    private String keyword;
    private String stage;
    private Long ownerId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
