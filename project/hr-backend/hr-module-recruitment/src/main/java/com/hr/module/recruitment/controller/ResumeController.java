package com.hr.module.recruitment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.*;
import com.hr.module.recruitment.service.RecResumeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "简历管理")
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final RecResumeService recResumeService;

    @Operation(summary = "简历列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('recruitment:resume:list')")
    public Result<IPage<ResumeVO>> list(ResumeQuery query) {
        return Result.success(recResumeService.page(query));
    }

    @Operation(summary = "简历详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:resume:list')")
    public Result<ResumeVO> getById(@PathVariable Long id) {
        return Result.success(recResumeService.getById(id));
    }

    @Operation(summary = "创建简历")
    @PostMapping
    @PreAuthorize("hasAuthority('recruitment:resume:create')")
    public Result<Void> create(@Valid @RequestBody ResumeDTO dto) {
        recResumeService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑简历")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:resume:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ResumeDTO dto) {
        recResumeService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除简历")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:resume:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        recResumeService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量导入")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('recruitment:resume:import')")
    public Result<Void> importExcel() {
        // TODO: 实现 Excel 批量导入
        return Result.success();
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('recruitment:resume:delete')")
    public Result<Void> batchDelete(@Valid @RequestBody BatchDeleteDTO dto) {
        recResumeService.batchDelete(dto.getIds());
        return Result.success();
    }

    @Operation(summary = "发送面试邀请")
    @PostMapping("/{id}/invite")
    @PreAuthorize("hasAuthority('recruitment:resume:invite')")
    public Result<Void> invite(@PathVariable Long id, @Valid @RequestBody InterviewInviteDTO dto) {
        recResumeService.invite(id, dto);
        return Result.success();
    }
}
