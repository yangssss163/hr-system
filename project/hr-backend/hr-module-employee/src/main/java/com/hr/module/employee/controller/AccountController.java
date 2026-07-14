package com.hr.module.employee.controller;

import com.hr.common.result.Result;
import com.hr.module.employee.dto.AccountDTO;
import com.hr.module.employee.service.HrAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "账号管理")
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final HrAccountService hrAccountService;

    @Operation(summary = "为员工开通系统账号")
    @PostMapping("/open")
    @PreAuthorize("hasAuthority('employee:account:create')")
    public Result<Void> openAccount(@Valid @RequestBody AccountDTO dto) {
        hrAccountService.openAccount(dto);
        return Result.success();
    }

    @Operation(summary = "启用/禁用账号")
    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('employee:account:toggle')")
    public Result<Void> toggleAccount(@PathVariable Long id) {
        hrAccountService.toggleAccount(id);
        return Result.success();
    }
}
