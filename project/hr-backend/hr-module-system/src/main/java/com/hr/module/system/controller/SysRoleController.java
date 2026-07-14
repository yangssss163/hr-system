package com.hr.module.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.result.Result;
import com.hr.module.system.dto.RoleMenuDTO;
import com.hr.module.system.dto.RoleSimpleVO;
import com.hr.module.system.dto.RoleVO;
import com.hr.module.system.entity.SysRole;
import com.hr.module.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Operation(summary = "角色列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    public Result<Page<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(sysRoleService.page(page, pageSize, keyword));
    }

    @Operation(summary = "全部角色（不分页，用于下拉选择）")
    @GetMapping("/all")
    public Result<List<RoleSimpleVO>> all() {
        return Result.success(sysRoleService.all());
    }

    @Operation(summary = "角色详情（含菜单权限）")
    @GetMapping("/{id}")
    public Result<RoleVO> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    public Result<Void> create(@RequestBody SysRole role) {
        sysRoleService.create(role);
        return Result.success();
    }

    @Operation(summary = "编辑角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysRole role) {
        role.setId(id);
        sysRoleService.update(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.delete(id);
        return Result.success();
    }

    @Operation(summary = "分配菜单权限")
    @PutMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('system:role:assign-menu')")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody RoleMenuDTO dto) {
        sysRoleService.assignMenus(id, dto.getMenuIds());
        return Result.success();
    }
}
