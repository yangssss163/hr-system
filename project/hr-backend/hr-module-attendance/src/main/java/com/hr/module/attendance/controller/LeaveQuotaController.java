package com.hr.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttLeaveQuotaAdjustDTO;
import com.hr.module.attendance.dto.AttLeaveQuotaQuery;
import com.hr.module.attendance.dto.AttLeaveQuotaVO;
import com.hr.module.attendance.service.AttLeaveQuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "假期额度")
@RestController
@RequestMapping("/api/leave-quotas")
@RequiredArgsConstructor
public class LeaveQuotaController {

    private final AttLeaveQuotaService attLeaveQuotaService;

    @Operation(summary = "列表分页")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:leave-quota:list')")
    public Result<IPage<AttLeaveQuotaVO>> list(AttLeaveQuotaQuery query) {
        return Result.success(attLeaveQuotaService.page(query));
    }

    @Operation(summary = "调整")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:leave-quota:adjust')")
    public Result<Void> adjust(@PathVariable Long id, @Valid @RequestBody AttLeaveQuotaAdjustDTO dto) {
        attLeaveQuotaService.adjust(id, dto);
        return Result.success();
    }
}
