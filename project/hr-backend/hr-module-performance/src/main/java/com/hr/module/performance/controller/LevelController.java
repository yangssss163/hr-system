package com.hr.module.performance.controller;

import com.hr.common.result.Result;
import com.hr.module.performance.dto.PerfLevelDTO;
import com.hr.module.performance.service.PerfLevelService;
import com.hr.module.performance.vo.PerfLevelVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "绩效等级")
@RestController
@RequestMapping("/api/perf-levels")
@RequiredArgsConstructor
public class LevelController {

    private final PerfLevelService perfLevelService;

    @Operation(summary = "绩效等级列表")
    @GetMapping
    @PreAuthorize("hasAuthority('perf:level:list')")
    public Result<List<PerfLevelVO>> list() {
        return Result.success(perfLevelService.list());
    }

    @Operation(summary = "绩效等级详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:level:list')")
    public Result<PerfLevelVO> getById(@PathVariable Long id) {
        return Result.success(perfLevelService.getById(id));
    }

    @Operation(summary = "创建绩效等级")
    @PostMapping
    @PreAuthorize("hasAuthority('perf:level:create')")
    public Result<Void> create(@Valid @RequestBody PerfLevelDTO dto) {
        perfLevelService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑绩效等级")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:level:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PerfLevelDTO dto) {
        perfLevelService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除绩效等级")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('perf:level:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        perfLevelService.delete(id);
        return Result.success();
    }
}
