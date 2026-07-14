package com.hr.module.office.controller;

import com.hr.common.result.Result;
import com.hr.module.office.dto.SysScheduleDTO;
import com.hr.module.office.dto.SysScheduleQuery;
import com.hr.module.office.dto.SysScheduleVO;
import com.hr.module.office.service.SysScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "日程管理")
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final SysScheduleService sysScheduleService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('office:schedule:list')")
    public Result<?> list(SysScheduleQuery query) {
        return Result.success(sysScheduleService.page(query));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('office:schedule:list')")
    public Result<SysScheduleVO> detail(@PathVariable Long id) {
        return Result.success(sysScheduleService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('office:schedule:create')")
    public Result<Void> create(@Valid @RequestBody SysScheduleDTO dto) {
        sysScheduleService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('office:schedule:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysScheduleDTO dto) {
        sysScheduleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('office:schedule:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysScheduleService.delete(id);
        return Result.success();
    }
}
