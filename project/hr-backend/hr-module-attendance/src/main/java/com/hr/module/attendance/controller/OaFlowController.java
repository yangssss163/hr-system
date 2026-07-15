package com.hr.module.attendance.controller;

import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttOaFlowQuery;
import com.hr.module.attendance.dto.AttOaFlowVO;
import com.hr.module.attendance.service.AttOaFlowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "OA流程管理")
@RestController
@RequestMapping("/api/oa-flows")
@RequiredArgsConstructor
public class OaFlowController {

    private final AttOaFlowService attOaFlowService;

    @Operation(summary = "列表分页")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:oa-flow:list')")
    public Result<PageResult<AttOaFlowVO>> list(AttOaFlowQuery query) {
        return Result.success(attOaFlowService.page(query));
    }

    @Operation(summary = "导入")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('attendance:oa-flow:import')")
    public Result<Void> importFlows(@RequestParam("file") MultipartFile file) throws IOException {
        attOaFlowService.importFlows(file);
        return Result.success();
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:oa-flow:view')")
    public Result<AttOaFlowVO> getById(@PathVariable Long id) {
        return Result.success(attOaFlowService.getById(id));
    }
}
