package com.hr.module.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotifyTemplateDTO {
    @NotBlank(message = "模板名称不能为空")
    private String name;

    @NotBlank(message = "类型不能为空")
    private String type;

    @NotBlank(message = "标题不能为空")
    private String title;

    private String content;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
