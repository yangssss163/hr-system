package com.hr.module.attendance.controller;

import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttLeaveTypeDTO;
import com.hr.module.attendance.dto.AttLeaveTypeVO;
import com.hr.module.attendance.service.AttLeaveTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "假期设置")
@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final AttLeaveTypeService attLeaveTypeService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:leave-type:list')")
    public Result<List<AttLeaveTypeVO>> list() {
        return Result.success(attLeaveTypeService.list());
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('attendance:leave-type:create')")
    public Result<Void> create(@Valid @RequestBody AttLeaveTypeDTO dto) {
        attLeaveTypeService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:leave-type:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttLeaveTypeDTO dto) {
        attLeaveTypeService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:leave-type:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        attLeaveTypeService.delete(id);
        return Result.success();
    }
}
