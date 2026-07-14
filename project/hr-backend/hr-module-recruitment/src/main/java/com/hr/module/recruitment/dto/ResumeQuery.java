package com.hr.module.recruitment.dto;

import lombok.Data;

@Data
public class ResumeQuery {
    private String keyword;
    private String status;
    private String education;
    private String source;
    private String applyPosition;
    private String createTimeStart;
    private String createTimeEnd;
    private Integer page = 1;
    private Integer pageSize = 10;
}
