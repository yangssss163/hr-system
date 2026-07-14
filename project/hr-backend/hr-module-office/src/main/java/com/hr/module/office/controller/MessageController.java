package com.hr.module.office.controller;

import com.hr.common.result.Result;
import com.hr.module.office.dto.SysMessageDTO;
import com.hr.module.office.dto.SysMessageQuery;
import com.hr.module.office.dto.SysMessageVO;
import com.hr.module.office.service.SysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息管理")
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SysMessageService sysMessageService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('office:message:list')")
    public Result<?> list(SysMessageQuery query) {
        return Result.success(sysMessageService.page(query));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('office:message:list')")
    public Result<SysMessageVO> detail(@PathVariable Long id) {
        return Result.success(sysMessageService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('office:message:create')")
    public Result<Void> create(@Valid @RequestBody SysMessageDTO dto) {
        sysMessageService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('office:message:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysMessageDTO dto) {
        sysMessageService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('office:message:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysMessageService.delete(id);
        return Result.success();
    }
}
