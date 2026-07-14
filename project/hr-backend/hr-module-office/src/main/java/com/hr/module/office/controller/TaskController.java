package com.hr.module.office.controller;

import com.hr.common.result.Result;
import com.hr.module.office.dto.SysTaskDTO;
import com.hr.module.office.dto.SysTaskQuery;
import com.hr.module.office.dto.SysTaskVO;
import com.hr.module.office.service.SysTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务管理")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final SysTaskService sysTaskService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('office:task:list')")
    public Result<?> list(SysTaskQuery query) {
        return Result.success(sysTaskService.page(query));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('office:task:list')")
    public Result<SysTaskVO> detail(@PathVariable Long id) {
        return Result.success(sysTaskService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('office:task:create')")
    public Result<Void> create(@Valid @RequestBody SysTaskDTO dto) {
        sysTaskService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('office:task:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysTaskDTO dto) {
        sysTaskService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('office:task:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysTaskService.delete(id);
        return Result.success();
    }
}
