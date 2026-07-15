package com.hr.module.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttCardRuleDTO {
    @NotBlank(message = "规则名称不能为空")
    private String name;

    @NotNull(message = "最少打卡次数不能为空")
    private Integer minCardCount;

    @NotNull(message = "允许加班不能为空")
    private Boolean allowOvertime;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
