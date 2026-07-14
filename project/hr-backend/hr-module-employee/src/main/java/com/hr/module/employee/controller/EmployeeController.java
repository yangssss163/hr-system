package com.hr.module.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.employee.dto.BatchDeleteDTO;
import com.hr.module.employee.dto.EmployeeDTO;
import com.hr.module.employee.dto.EmployeeQuery;
import com.hr.module.employee.dto.EmployeeVO;
import com.hr.module.employee.service.HrEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "员工管理")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final HrEmployeeService hrEmployeeService;

    @Operation(summary = "员工花名册列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('employee:list')")
    public Result<IPage<EmployeeVO>> list(EmployeeQuery query) {
        return Result.success(hrEmployeeService.page(query));
    }

    @Operation(summary = "员工详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:list')")
    public Result<EmployeeVO> getById(@PathVariable Long id) {
        return Result.success(hrEmployeeService.getById(id));
    }

    @Operation(summary = "新增员工")
    @PostMapping
    @PreAuthorize("hasAuthority('employee:create')")
    public Result<Void> create(@Valid @RequestBody EmployeeDTO dto) {
        hrEmployeeService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑员工")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EmployeeDTO dto) {
        hrEmployeeService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        hrEmployeeService.delete(id);
        return Result.success();
    }

    @Operation(summary = "批量删除")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('employee:delete')")
    public Result<Void> batchDelete(@Valid @RequestBody BatchDeleteDTO dto) {
        hrEmployeeService.batchDelete(dto.getIds());
        return Result.success();
    }

    @Operation(summary = "批量导入（Excel）")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('employee:import')")
    public Result<Void> importExcel() {
        // TODO: 实现 Excel 批量导入
        return Result.success();
    }

    @Operation(summary = "导出 Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('employee:export')")
    public Result<Void> exportExcel() {
        // TODO: 实现 Excel 导出
        return Result.success();
    }
}
