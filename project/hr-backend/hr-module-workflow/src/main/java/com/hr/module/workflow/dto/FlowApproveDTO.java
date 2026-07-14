package com.hr.module.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlowApproveDTO {
    @NotNull(message = "审批不能为空")
    private Long id;
    @NotBlank(message = "审批结果不能为空")
    private String result;
    private String comment;
}
