package com.hr.module.recruitment.dto;

import lombok.Data;

@Data
public class InterviewInviteDTO {
    private String interviewTime;
    private String location;
    private Long templateId;
    private String message;
}
