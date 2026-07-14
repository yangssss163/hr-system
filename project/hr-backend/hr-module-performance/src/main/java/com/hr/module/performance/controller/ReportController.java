package com.hr.module.performance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.performance.service.PerfReportService;
import com.hr.module.performance.vo.PerfRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "绩效报表")
@RestController
@RequestMapping("/api/perf-reports")
@RequiredArgsConstructor
public class ReportController {

    private final PerfReportService perfReportService;

    @Operation(summary = "绩效明细表")
    @GetMapping("/detail")
    @PreAuthorize("hasAuthority('perf:report:detail')")
    public Result<IPage<PerfRecordVO>> detail(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(perfReportService.detail(planId, deptId, page, pageSize));
    }

    @Operation(summary = "部门绩效汇总表")
    @GetMapping("/dept-summary")
    @PreAuthorize("hasAuthority('perf:report:dept-summary')")
    public Result<List<Map<String, Object>>> deptSummary() {
        return Result.success(perfReportService.deptSummary());
    }

    @Operation(summary = "职员绩效汇总表")
    @GetMapping("/employee-summary")
    @PreAuthorize("hasAuthority('perf:report:employee-summary')")
    public Result<IPage<PerfRecordVO>> employeeSummary(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(perfReportService.employeeSummary(page, pageSize));
    }
}
