package com.hr.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttRecordBatchFixDTO;
import com.hr.module.attendance.dto.AttRecordQuery;
import com.hr.module.attendance.dto.AttRecordVO;
import com.hr.module.attendance.service.AttRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "打卡记录")
@RestController
@RequestMapping("/api/attendance-records")
@RequiredArgsConstructor
public class AttRecordController {

    private final AttRecordService attRecordService;

    @Operation(summary = "列表分页")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:record:list')")
    public Result<IPage<AttRecordVO>> list(AttRecordQuery query) {
        return Result.success(attRecordService.page(query));
    }

    @Operation(summary = "导入")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('attendance:record:import')")
    public Result<Void> importRecords(@RequestBody AttRecordBatchFixDTO dto) {
        // TODO: import logic — consider extracting to service
        return Result.success();
    }

    @Operation(summary = "批量修正")
    @PutMapping("/batch-fix")
    @PreAuthorize("hasAuthority('attendance:record:fix')")
    public Result<Void> batchFix(@Valid @RequestBody AttRecordBatchFixDTO dto) {
        attRecordService.batchFix(dto);
        return Result.success();
    }
}
