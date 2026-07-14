package com.hr.module.system.controller;

import com.hr.common.result.Result;
import com.hr.module.system.dto.CompanyTreeVO;
import com.hr.module.system.entity.SysCompany;
import com.hr.module.system.service.SysCompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "公司管理")
@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class SysCompanyController {

    private final SysCompanyService sysCompanyService;

    @Operation(summary = "获取公司树")
    @GetMapping("/tree")
    public Result<List<CompanyTreeVO>> tree() {
        return Result.success(sysCompanyService.tree());
    }

    @Operation(summary = "获取公司详情")
    @GetMapping("/{id}")
    public Result<SysCompany> getById(@PathVariable Long id) {
        return Result.success(sysCompanyService.getById(id));
    }

    @Operation(summary = "创建公司")
    @PostMapping
    @PreAuthorize("hasAuthority('system:company:create')")
    public Result<Void> create(@RequestBody SysCompany company) {
        sysCompanyService.create(company);
        return Result.success();
    }

    @Operation(summary = "编辑公司")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:company:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysCompany company) {
        company.setId(id);
        sysCompanyService.update(company);
        return Result.success();
    }

    @Operation(summary = "删除公司")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:company:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysCompanyService.delete(id);
        return Result.success();
    }
}
