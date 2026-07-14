package com.hr.module.workflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("flow_leave")
public class FlowLeave extends BaseEntity {
    private Long applicantId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double days;
    private String reason;
    private String status;
    private Long approverId;
    private LocalDateTime approveTime;
    private String approveComment;
}
