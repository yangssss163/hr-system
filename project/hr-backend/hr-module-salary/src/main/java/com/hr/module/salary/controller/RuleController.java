package com.hr.module.salary.controller;

import com.hr.common.result.Result;
import com.hr.module.salary.dto.SalRuleEditDTO;
import com.hr.module.salary.dto.SalRuleVO;
import com.hr.module.salary.service.SalRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "薪酬规则管理")
@RestController
@RequestMapping("/api/salary-rules")
@RequiredArgsConstructor
public class RuleController {

    private final SalRuleService salRuleService;

    @Operation(summary = "规则列表")
    @GetMapping("/")
    @PreAuthorize("hasAuthority('salary:rule:list')")
    public Result<List<SalRuleVO>> list() {
        return Result.success(salRuleService.list());
    }

    @Operation(summary = "编辑规则")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:rule:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SalRuleEditDTO dto) {
        salRuleService.update(id, dto);
        return Result.success();
    }
}
