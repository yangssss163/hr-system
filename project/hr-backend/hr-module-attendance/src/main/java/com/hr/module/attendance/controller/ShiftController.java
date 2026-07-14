package com.hr.module.attendance.controller;

import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttShiftDTO;
import com.hr.module.attendance.dto.AttShiftVO;
import com.hr.module.attendance.service.AttShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "班次设置")
@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final AttShiftService attShiftService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:shift:list')")
    public Result<List<AttShiftVO>> list() {
        return Result.success(attShiftService.list());
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:shift:list')")
    public Result<AttShiftVO> getById(@PathVariable Long id) {
        return Result.success(attShiftService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('attendance:shift:create')")
    public Result<Void> create(@Valid @RequestBody AttShiftDTO dto) {
        attShiftService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:shift:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttShiftDTO dto) {
        attShiftService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:shift:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        attShiftService.delete(id);
        return Result.success();
    }
}
