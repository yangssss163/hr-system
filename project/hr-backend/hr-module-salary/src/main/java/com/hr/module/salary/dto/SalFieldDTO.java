package com.hr.module.salary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalFieldDTO {
    @NotBlank(message = "名称不能为空")
    private String name;

    @NotBlank(message = "编码不能为空")
    private String code;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String formula;
    private Integer sort;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
