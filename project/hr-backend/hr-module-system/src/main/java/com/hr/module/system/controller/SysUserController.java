package com.hr.module.system.controller;

import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.system.dto.UserDTO;
import com.hr.module.system.dto.UserQuery;
import com.hr.module.system.dto.UserRoleDTO;
import com.hr.module.system.dto.UserVO;
import com.hr.module.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('system:user:list')")
    public Result<PageResult<UserVO>> list(UserQuery query) {
        return Result.success(sysUserService.page(query));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:create')")
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        sysUserService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        sysUserService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }

    @Operation(summary = "分配角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:assign-role')")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody UserRoleDTO dto) {
        sysUserService.assignRoles(id, dto.getRoleIds());
        return Result.success();
    }
}
