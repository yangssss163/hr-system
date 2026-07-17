package com.hr.module.salary.controller;

import com.hr.common.result.Result;
import com.hr.module.salary.dto.SalFieldDTO;
import com.hr.module.salary.dto.SalFieldVO;
import com.hr.module.salary.service.SalFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "工资项字段管理")
@RestController
@RequestMapping("/api/salary-fields")
@RequiredArgsConstructor
public class FieldController {

    private final SalFieldService salFieldService;

    @Operation(summary = "字段列表")
    @GetMapping
    @PreAuthorize("hasAuthority('salary:field:list')")
    public Result<List<SalFieldVO>> list() {
        return Result.success(salFieldService.list());
    }

    @Operation(summary = "字段详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:field:list')")
    public Result<SalFieldVO> detail(@PathVariable Long id) {
        return Result.success(salFieldService.getById(id));
    }

    @Operation(summary = "创建字段")
    @PostMapping
    @PreAuthorize("hasAuthority('salary:field:create')")
    public Result<Void> create(@Valid @RequestBody SalFieldDTO dto) {
        salFieldService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑字段")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:field:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SalFieldDTO dto) {
        salFieldService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除字段")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:field:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        salFieldService.delete(id);
        return Result.success();
    }
}
