package com.hr.module.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterviewOfferDTO {
    @NotBlank(message = "薪资不能为空")
    private String offerSalary;
    @NotBlank(message = "入职日期不能为空")
    private String offerDate;
    private String remark;
}
