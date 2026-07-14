package com.hr.module.recruitment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.NotifyTemplateDTO;
import com.hr.module.recruitment.dto.NotifyTemplateVO;
import com.hr.module.recruitment.service.RecNotifyTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "通知模板")
@RestController
@RequestMapping("/api/notify-templates")
@RequiredArgsConstructor
public class NotifyTemplateController {

    private final RecNotifyTemplateService recNotifyTemplateService;

    @Operation(summary = "模板列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('recruitment:notify-template:list')")
    public Result<IPage<NotifyTemplateVO>> list(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(recNotifyTemplateService.page(page, pageSize));
    }

    @Operation(summary = "模板详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:notify-template:list')")
    public Result<NotifyTemplateVO> getById(@PathVariable Long id) {
        return Result.success(recNotifyTemplateService.getById(id));
    }

    @Operation(summary = "创建模板")
    @PostMapping
    @PreAuthorize("hasAuthority('recruitment:notify-template:create')")
    public Result<Void> create(@Valid @RequestBody NotifyTemplateDTO dto) {
        recNotifyTemplateService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑模板")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:notify-template:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody NotifyTemplateDTO dto) {
        recNotifyTemplateService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除模板")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:notify-template:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        recNotifyTemplateService.delete(id);
        return Result.success();
    }
}
