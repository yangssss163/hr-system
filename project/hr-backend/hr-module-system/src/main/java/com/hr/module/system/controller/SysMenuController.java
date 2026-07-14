package com.hr.module.system.controller;

import com.hr.common.result.Result;
import com.hr.module.system.dto.MenuTreeVO;
import com.hr.module.system.entity.SysMenu;
import com.hr.module.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "获取菜单树")
    @GetMapping("/tree")
    public Result<List<MenuTreeVO>> tree() {
        return Result.success(sysMenuService.tree());
    }

    @Operation(summary = "获取菜单详情")
    @GetMapping("/{id}")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.getById(id));
    }

    @Operation(summary = "创建菜单")
    @PostMapping
    @PreAuthorize("hasAuthority('system:menu:create')")
    public Result<Void> create(@RequestBody SysMenu menu) {
        sysMenuService.create(menu);
        return Result.success();
    }

    @Operation(summary = "编辑菜单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        sysMenuService.update(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.delete(id);
        return Result.success();
    }
}
