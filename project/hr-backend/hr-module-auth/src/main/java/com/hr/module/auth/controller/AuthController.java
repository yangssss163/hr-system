package com.hr.module.auth.controller;

import com.hr.common.result.Result;
import com.hr.module.auth.dto.LoginDTO;
import com.hr.module.auth.dto.RegisterDTO;
import com.hr.module.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        authService.register(dto);
        return Result.success();
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/userinfo")
    public Result<Map<String, Object>> getUserInfo() {
        return Result.success(authService.getUserInfo());
    }

    @Operation(summary = "获取当前用户菜单")
    @GetMapping("/menus")
    public Result<?> getMenus() {
        return Result.success(authService.getMenus());
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        authService.updatePassword(params.get("oldPassword"), params.get("newPassword"));
        return Result.success();
    }
}
