package com.hr.module.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_interview")
public class RecInterview extends BaseEntity {
    private Long resumeId;
    private Integer interviewRound;
    private Long interviewerId;
    private LocalDateTime interviewTime;
    private String location;
    private String result;
    private BigDecimal score;
    private String evaluation;
    private String reason;
    private String offerSalary;
    private LocalDate offerDate;
    private String offerRemark;
}
