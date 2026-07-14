package com.hr.module.recruitment.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecruitmentReportVO {
    private Long totalResumes;
    private Long totalInterviews;
    private Long totalPassed;
    private Long totalHired;
    private Double conversionRate;
    private List<ChannelStatVO> channelStats;
    private List<MonthlyStatVO> monthlyStats;
}
