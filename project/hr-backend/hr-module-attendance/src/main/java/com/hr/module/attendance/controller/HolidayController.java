package com.hr.module.attendance.controller;

import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttHolidayDTO;
import com.hr.module.attendance.dto.AttHolidayVO;
import com.hr.module.attendance.service.AttHolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "法定假期")
@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final AttHolidayService attHolidayService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:holiday:list')")
    public Result<AttHolidayVO> list(@RequestParam Integer year) {
        return Result.success(attHolidayService.list(year));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('attendance:holiday:create')")
    public Result<Void> create(@Valid @RequestBody AttHolidayDTO dto) {
        attHolidayService.create(dto);
        return Result.success();
    }

    @Operation(summary = "修改")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:holiday:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttHolidayDTO dto) {
        attHolidayService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "复制")
    @PostMapping("/{id}/copy")
    @PreAuthorize("hasAuthority('attendance:holiday:copy')")
    public Result<Void> copy(@PathVariable Long id, @RequestParam Integer targetYear) {
        attHolidayService.copy(id, targetYear);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:holiday:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        attHolidayService.delete(id);
        return Result.success();
    }
}
