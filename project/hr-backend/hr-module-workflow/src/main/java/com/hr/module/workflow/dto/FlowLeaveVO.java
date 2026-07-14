package com.hr.module.workflow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlowLeaveVO {
    private Long id;
    private Long applicantId;
    private String applicantName;
    private String leaveType;
    private String startDate;
    private String endDate;
    private Double days;
    private String reason;
    private String status;
    private String statusName;
    private Long approverId;
    private String approverName;
    private LocalDateTime createTime;
}
