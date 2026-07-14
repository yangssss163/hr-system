package com.hr.module.attendance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttExceptionQuery;
import com.hr.module.attendance.dto.AttExceptionVO;
import com.hr.module.attendance.service.AttExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "异常统计")
@RestController
@RequestMapping("/api/attendance-exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final AttExceptionService attExceptionService;

    @Operation(summary = "列表分页")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:exception:list')")
    public Result<IPage<AttExceptionVO>> list(AttExceptionQuery query) {
        return Result.success(attExceptionService.page(query));
    }
}
