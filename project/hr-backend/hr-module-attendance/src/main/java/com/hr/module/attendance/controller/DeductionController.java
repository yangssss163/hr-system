package com.hr.module.attendance.controller;

import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttDeductionEditDTO;
import com.hr.module.attendance.dto.AttDeductionVO;
import com.hr.module.attendance.service.AttDeductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "考勤扣款")
@RestController
@RequestMapping("/api/attendance-deductions")
@RequiredArgsConstructor
public class DeductionController {

    private final AttDeductionService attDeductionService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:deduction:list')")
    public Result<List<AttDeductionVO>> list() {
        return Result.success(attDeductionService.list());
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:deduction:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttDeductionEditDTO dto) {
        attDeductionService.update(id, dto);
        return Result.success();
    }
}
