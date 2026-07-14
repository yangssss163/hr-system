package com.hr.module.recruitment.dto;

import lombok.Data;

@Data
public class InterviewQuery {
    private String keyword;
    private String result;
    private Long interviewerId;
    private String interviewDateStart;
    private String interviewDateEnd;
    private Integer page = 1;
    private Integer pageSize = 10;
}
