package com.hr.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;
import com.hr.module.attendance.service.AttReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "考勤报表")
@RestController
@RequestMapping("/api/attendance-reports")
@RequiredArgsConstructor
public class AttendanceReportController {

    private final AttReportService attReportService;

    @Operation(summary = "明细表")
    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('attendance:report:detail')")
    public Result<IPage<AttReportSummaryVO>> detail(AttReportDetailQuery query) {
        return Result.success(attReportService.detail(query));
    }

    @Operation(summary = "汇总表")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('attendance:report:summary')")
    public Result<List<AttReportSummaryVO>> summary(@RequestParam String deptId, @RequestParam String month) {
        return Result.success(attReportService.summary(deptId, month));
    }
}
