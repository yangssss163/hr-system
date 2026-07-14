package com.hr.module.office.controller;

import com.hr.common.result.Result;
import com.hr.module.office.dto.SysNoticeDTO;
import com.hr.module.office.dto.SysNoticeQuery;
import com.hr.module.office.dto.SysNoticeVO;
import com.hr.module.office.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "公告管理")
@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final SysNoticeService sysNoticeService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('office:notice:list')")
    public Result<?> list(SysNoticeQuery query) {
        return Result.success(sysNoticeService.page(query));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('office:notice:list')")
    public Result<SysNoticeVO> detail(@PathVariable Long id) {
        return Result.success(sysNoticeService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('office:notice:create')")
    public Result<Void> create(@Valid @RequestBody SysNoticeDTO dto) {
        sysNoticeService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('office:notice:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysNoticeDTO dto) {
        sysNoticeService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('office:notice:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysNoticeService.delete(id);
        return Result.success();
    }
}
