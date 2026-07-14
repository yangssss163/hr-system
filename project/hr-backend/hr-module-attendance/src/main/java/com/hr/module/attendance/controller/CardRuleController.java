package com.hr.module.attendance.controller;

import com.hr.common.result.Result;
import com.hr.module.attendance.dto.AttCardRuleDTO;
import com.hr.module.attendance.dto.AttCardRuleVO;
import com.hr.module.attendance.service.AttCardRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "取卡规则")
@RestController
@RequestMapping("/api/card-rules")
@RequiredArgsConstructor
public class CardRuleController {

    private final AttCardRuleService attCardRuleService;

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('attendance:card-rule:list')")
    public Result<List<AttCardRuleVO>> list() {
        return Result.success(attCardRuleService.list());
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:card-rule:list')")
    public Result<AttCardRuleVO> getById(@PathVariable Long id) {
        return Result.success(attCardRuleService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('attendance:card-rule:create')")
    public Result<Void> create(@Valid @RequestBody AttCardRuleDTO dto) {
        attCardRuleService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:card-rule:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody AttCardRuleDTO dto) {
        attCardRuleService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('attendance:card-rule:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        attCardRuleService.delete(id);
        return Result.success();
    }
}
