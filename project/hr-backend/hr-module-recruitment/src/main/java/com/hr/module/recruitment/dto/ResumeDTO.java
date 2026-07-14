package com.hr.module.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeDTO {
    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String email;
    private Integer gender;
    private String education;
    private String school;
    private String major;
    private Integer workYears;
    private String applyPosition;
    private String source;
    private String status;
}
