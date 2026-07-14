package com.hr.module.workflow.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlowTravelVO {
    private Long id;
    private Long applicantId;
    private String applicantName;
    private String destination;
    private String startDate;
    private String endDate;
    private Double days;
    private String reason;
    private String companions;
    private BigDecimal budget;
    private String status;
    private String statusName;
    private Long approverId;
    private String approverName;
    private LocalDateTime createTime;
}
