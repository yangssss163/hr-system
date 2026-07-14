package com.hr.module.recruitment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterviewVO {
    private Long id;
    private Long resumeId;
    private String candidateName;
    private String applyPosition;
    private Integer interviewRound;
    private Long interviewerId;
    private String interviewerName;
    private LocalDateTime interviewTime;
    private String location;
    private String result;
    private BigDecimal score;
    private String evaluation;
    private LocalDateTime createTime;
}
