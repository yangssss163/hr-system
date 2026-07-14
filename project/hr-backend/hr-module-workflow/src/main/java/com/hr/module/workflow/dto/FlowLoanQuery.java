package com.hr.module.workflow.dto;

import lombok.Data;

@Data
public class FlowLoanQuery {
    private Long applicantId;
    private String status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
