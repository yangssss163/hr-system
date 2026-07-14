package com.hr.module.recruitment.dto;

import lombok.Data;

@Data
public class MonthlyStatVO {
    private String month;
    private Long resumes;
    private Long interviews;
    private Long hired;
}
