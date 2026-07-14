package com.hr.module.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.salary.dto.*;
import com.hr.module.salary.service.SalSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "工资表管理")
@RestController
@RequestMapping("/api/salary-sheets")
@RequiredArgsConstructor
public class SheetController {

    private final SalSheetService salSheetService;

    @Operation(summary = "工资表列表")
    @GetMapping
    @PreAuthorize("hasAuthority('salary:sheet:list')")
    public Result<IPage<SalSheetVO>> list(@Valid SalSheetQuery query) {
        return Result.success(salSheetService.page(query));
    }

    @Operation(summary = "同步绩效考勤数据")
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('salary:sheet:sync')")
    public Result<Void> sync(@Valid @RequestBody SalSheetSyncDTO dto) {
        salSheetService.sync(dto);
        return Result.success();
    }

    @Operation(summary = "生成工资表")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('salary:sheet:generate')")
    public Result<Void> generate(@Valid @RequestBody SalSheetGenerateDTO dto) {
        salSheetService.generate(dto);
        return Result.success();
    }

    @Operation(summary = "导入数据")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('salary:sheet:import')")
    public Result<Void> importData() {
        return Result.success();
    }

    @Operation(summary = "导出工资表")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('salary:sheet:export')")
    public Result<Void> export() {
        return Result.success();
    }
}
