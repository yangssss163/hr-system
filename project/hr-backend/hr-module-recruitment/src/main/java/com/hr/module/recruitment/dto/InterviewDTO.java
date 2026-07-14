package com.hr.module.recruitment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewDTO {
    @NotNull(message = "简历不能为空")
    private Long resumeId;

    @NotNull(message = "面试轮次不能为空")
    private Integer interviewRound;

    private Long interviewerId;

    @NotNull(message = "面试时间不能为空")
    private String interviewTime;

    private String location;
}
