package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmExpenseDTO;
import com.hr.module.crm.dto.CrmExpenseQuery;
import com.hr.module.crm.dto.CrmExpenseVO;
import com.hr.module.crm.service.CrmExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "费用管理")
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final CrmExpenseService crmExpenseService;

    @Operation(summary = "费用列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:expense:list')")
    public Result<PageResult<CrmExpenseVO>> list(@Valid CrmExpenseQuery query) {
        IPage<CrmExpenseVO> page = crmExpenseService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "费用详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:expense:list')")
    public Result<CrmExpenseVO> detail(@PathVariable Long id) {
        return Result.success(crmExpenseService.getById(id));
    }

    @Operation(summary = "新增费用")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:expense:create')")
    public Result<Void> create(@Valid @RequestBody CrmExpenseDTO dto) {
        crmExpenseService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑费用")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:expense:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmExpenseDTO dto) {
        crmExpenseService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除费用")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:expense:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmExpenseService.delete(id);
        return Result.success();
    }
}
