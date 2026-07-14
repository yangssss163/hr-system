package com.hr.module.system.controller;

import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.system.dto.DeptTreeVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/depts")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    @Operation(summary = "获取部门树")
    @GetMapping("/tree")
    public Result<List<DeptTreeVO>> tree(@RequestParam(required = false) Long companyId) {
        return Result.success(sysDeptService.tree(companyId));
    }

    @Operation(summary = "获取部门详情")
    @GetMapping("/{id}")
    public Result<SysDept> getById(@PathVariable Long id) {
        return Result.success(sysDeptService.getById(id));
    }

    @Operation(summary = "创建部门")
    @PostMapping
    @PreAuthorize("hasAuthority('system:dept:create')")
    public Result<Void> create(@RequestBody SysDept dept) {
        sysDeptService.create(dept);
        return Result.success();
    }

    @Operation(summary = "编辑部门")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysDept dept) {
        dept.setId(id);
        sysDeptService.update(dept);
        return Result.success();
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:dept:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysDeptService.delete(id);
        return Result.success();
    }
}
