package com.hr.module.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.salary.dto.SalAdjustDTO;
import com.hr.module.salary.dto.SalAdjustQuery;
import com.hr.module.salary.dto.SalAdjustVO;
import com.hr.module.salary.service.SalAdjustService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "调薪管理")
@RestController
@RequestMapping("/api/salary-adjusts")
@RequiredArgsConstructor
public class AdjustController {

    private final SalAdjustService salAdjustService;

    @Operation(summary = "调薪列表")
    @GetMapping
    @PreAuthorize("hasAuthority('salary:adjust:list')")
    public Result<PageResult<SalAdjustVO>> list(@Valid SalAdjustQuery query) {
        IPage<SalAdjustVO> page = salAdjustService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "调薪详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:adjust:list')")
    public Result<SalAdjustVO> detail(@PathVariable Long id) {
        return Result.success(salAdjustService.getById(id));
    }

    @Operation(summary = "员工调薪")
    @PostMapping
    @PreAuthorize("hasAuthority('salary:adjust:create')")
    public Result<Void> create(@Valid @RequestBody SalAdjustDTO dto) {
        salAdjustService.create(dto);
        return Result.success();
    }
}
