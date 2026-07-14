package com.hr.module.recruitment.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InterviewEvaluateDTO {
    private BigDecimal score;
    private String evaluation;
    private String result;
}
