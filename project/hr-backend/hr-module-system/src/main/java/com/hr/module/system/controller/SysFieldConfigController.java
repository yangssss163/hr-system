package com.hr.module.system.controller;

import com.hr.common.result.Result;
import com.hr.module.system.entity.SysFieldConfig;
import com.hr.module.system.service.SysFieldConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字段配置")
@RestController
@RequestMapping("/api/field-configs")
@RequiredArgsConstructor
public class SysFieldConfigController {

    private final SysFieldConfigService sysFieldConfigService;

    @Operation(summary = "字段配置列表")
    @GetMapping
    public Result<List<SysFieldConfig>> list(@RequestParam(required = false) String module) {
        return Result.success(sysFieldConfigService.list(module));
    }

    @Operation(summary = "批量保存字段配置")
    @PutMapping
    public Result<Void> batchSave(@RequestBody List<SysFieldConfig> configs) {
        sysFieldConfigService.batchSave(configs);
        return Result.success();
    }
}
