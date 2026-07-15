package com.hr.module.performance.controller;

import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.performance.dto.PerfRecordDTO;
import com.hr.module.performance.query.PerfRecordQuery;
import com.hr.module.performance.service.PerfRecordService;
import com.hr.module.performance.vo.PerfRecordDetailVO;
import com.hr.module.performance.vo.PerfRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "绩效考核记录")
@RestController
@RequestMapping("/api/perf-records")
@RequiredArgsConstructor
public class PerfRecordController {

    private final PerfRecordService perfRecordService;

    @Operation(summary = "绩效考核记录列表")
    @GetMapping
    @PreAuthorize("hasAuthority('perf:record:list')")
    public Result<PageResult<PerfRecordVO>> list(@Valid PerfRecordQuery query) {
        return Result.success(perfRecordService.page(query));
    }

    @Operation(summary = "绩效考核记录详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:record:list')")
    public Result<PerfRecordDetailVO> getById(@PathVariable Long id) {
        return Result.success(perfRecordService.getById(id));
    }

    @Operation(summary = "创建绩效考核记录")
    @PostMapping
    @PreAuthorize("hasAuthority('perf:record:create')")
    public Result<Void> create(@Valid @RequestBody PerfRecordDTO dto) {
        perfRecordService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑绩效考核记录")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:record:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PerfRecordDTO dto) {
        perfRecordService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除绩效考核记录")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:record:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        perfRecordService.delete(id);
        return Result.success();
    }
}
