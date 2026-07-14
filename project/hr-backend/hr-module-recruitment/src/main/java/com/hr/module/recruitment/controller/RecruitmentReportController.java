package com.hr.module.recruitment.controller;

import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.ChannelStatVO;
import com.hr.module.recruitment.dto.MonthlyStatVO;
import com.hr.module.recruitment.dto.RecruitmentReportVO;
import com.hr.module.recruitment.mapper.RecInterviewMapper;
import com.hr.module.recruitment.mapper.RecResumeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "招聘报表")
@RestController
@RequestMapping("/api/recruitment-reports")
@RequiredArgsConstructor
public class RecruitmentReportController {

    private final RecResumeMapper recResumeMapper;
    private final RecInterviewMapper recInterviewMapper;

    @Operation(summary = "招聘汇总报表")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('recruitment:report:list')")
    public Result<RecruitmentReportVO> summary() {
        RecruitmentReportVO report = new RecruitmentReportVO();

        // 总简历数
        Long totalResumes = recResumeMapper.selectCount(null);
        report.setTotalResumes(totalResumes);

        // 面试/通过/入职统计
        List<Map<String, Object>> interviewStats = recInterviewMapper.countByResult();
        long totalInterviews = 0, totalPassed = 0, totalHired = 0;
        for (Map<String, Object> stat : interviewStats) {
            String result = String.valueOf(stat.get("result"));
            long count = ((Number) stat.get("count")).longValue();
            totalInterviews += count;
            if ("pass".equals(result)) totalPassed += count;
            if ("hired".equals(result)) totalHired += count;
        }
        report.setTotalInterviews(totalInterviews);
        report.setTotalPassed(totalPassed);
        report.setTotalHired(totalHired);
        report.setConversionRate(totalResumes > 0 ? Math.round(totalHired * 10000.0 / totalResumes) / 100.0 : 0.0);

        // 渠道统计
        List<Map<String, Object>> channelStats = recInterviewMapper.countByChannel();
        List<ChannelStatVO> channelVOList = new ArrayList<>();
        for (Map<String, Object> stat : channelStats) {
            ChannelStatVO vo = new ChannelStatVO();
            vo.setChannel(String.valueOf(stat.get("channel")));
            long cnt = ((Number) stat.get("count")).longValue();
            vo.setCount(cnt);
            vo.setRate(totalResumes > 0 ? Math.round(cnt * 10000.0 / totalResumes) / 100.0 : 0.0);
            channelVOList.add(vo);
        }
        report.setChannelStats(channelVOList);

        // 月度统计
        List<Map<String, Object>> monthlyStats = recInterviewMapper.countMonthly();
        List<MonthlyStatVO> monthlyVOList = new ArrayList<>();
        for (Map<String, Object> stat : monthlyStats) {
            MonthlyStatVO vo = new MonthlyStatVO();
            vo.setMonth(String.valueOf(stat.get("month")));
            vo.setInterviews(((Number) stat.get("count")).longValue());
            monthlyVOList.add(vo);
        }
        report.setMonthlyStats(monthlyVOList);

        return Result.success(report);
    }
}
