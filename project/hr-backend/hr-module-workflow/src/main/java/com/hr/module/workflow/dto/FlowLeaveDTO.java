package com.hr.module.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlowLeaveDTO {
    @NotNull(message = "申请人不能为空")
    private Long applicantId;
    @NotBlank(message = "请假类型不能为空")
    private String leaveType;
    @NotBlank(message = "开始日期不能为空")
    private String startDate;
    @NotBlank(message = "结束日期不能为空")
    private String endDate;
    @NotNull(message = "天数不能为空")
    private Double days;
    private String reason;
}
