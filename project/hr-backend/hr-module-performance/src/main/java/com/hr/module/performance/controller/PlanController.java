package com.hr.module.performance.controller;

import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.performance.dto.PerfPlanDTO;
import com.hr.module.performance.query.PerfPlanQuery;
import com.hr.module.performance.service.PerfPlanService;
import com.hr.module.performance.vo.PerfPlanVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "绩效考核计划")
@RestController
@RequestMapping("/api/perf-plans")
@RequiredArgsConstructor
public class PlanController {

    private final PerfPlanService perfPlanService;

    @Operation(summary = "绩效考核计划列表")
    @GetMapping
    @PreAuthorize("hasAuthority('perf:plan:list')")
    public Result<PageResult<PerfPlanVO>> list(@Valid PerfPlanQuery query) {
        return Result.success(perfPlanService.page(query));
    }

    @Operation(summary = "绩效考核计划详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:plan:list')")
    public Result<PerfPlanVO> getById(@PathVariable Long id) {
        return Result.success(perfPlanService.getById(id));
    }

    @Operation(summary = "创建绩效考核计划")
    @PostMapping
    @PreAuthorize("hasAuthority('perf:plan:create')")
    public Result<Void> create(@Valid @RequestBody PerfPlanDTO dto) {
        perfPlanService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑绩效考核计划")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:plan:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PerfPlanDTO dto) {
        perfPlanService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除绩效考核计划")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:plan:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        perfPlanService.delete(id);
        return Result.success();
    }
}
