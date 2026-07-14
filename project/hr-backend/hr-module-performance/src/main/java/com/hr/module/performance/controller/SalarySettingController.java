package com.hr.module.performance.controller;

import com.hr.common.result.Result;
import com.hr.module.performance.dto.PerfSalaryDTO;
import com.hr.module.performance.service.PerfSalaryService;
import com.hr.module.performance.vo.PerfSalaryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "绩效工资")
@RestController
@RequestMapping("/api/perf-salaries")
@RequiredArgsConstructor
public class SalarySettingController {

    private final PerfSalaryService perfSalaryService;

    @Operation(summary = "绩效工资列表")
    @GetMapping
    @PreAuthorize("hasAuthority('perf:salary:list')")
    public Result<List<PerfSalaryVO>> list() {
        return Result.success(perfSalaryService.list());
    }

    @Operation(summary = "绩效工资详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:salary:list')")
    public Result<PerfSalaryVO> getById(@PathVariable Long id) {
        return Result.success(perfSalaryService.getById(id));
    }

    @Operation(summary = "创建绩效工资")
    @PostMapping
    @PreAuthorize("hasAuthority('perf:salary:create')")
    public Result<Void> create(@Valid @RequestBody PerfSalaryDTO dto) {
        perfSalaryService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑绩效工资")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:salary:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PerfSalaryDTO dto) {
        perfSalaryService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除绩效工资")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:salary:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        perfSalaryService.delete(id);
        return Result.success();
    }
}
