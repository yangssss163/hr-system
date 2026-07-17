package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowExpenseDTO;
import com.hr.module.workflow.dto.FlowExpenseQuery;
import com.hr.module.workflow.dto.FlowExpenseVO;
import com.hr.module.workflow.service.FlowExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "报销审批")
@RestController
@RequestMapping("/api/workflow/expenses")
@RequiredArgsConstructor
public class ExpenseApprovalController {

    private final FlowExpenseService flowExpenseService;

    @Operation(summary = "报销列表")
    @GetMapping
    @PreAuthorize("hasAuthority('workflow:expense:list')")
    public Result<IPage<FlowExpenseVO>> list(FlowExpenseQuery query) {
        return Result.success(flowExpenseService.page(query));
    }

    @Operation(summary = "报销详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:list')")
    public Result<FlowExpenseVO> detail(@PathVariable Long id) {
        return Result.success(flowExpenseService.getById(id));
    }

    @Operation(summary = "申请报销")
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:expense:create')")
    public Result<Void> create(@Valid @RequestBody FlowExpenseDTO dto) {
        flowExpenseService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑报销")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FlowExpenseDTO dto) {
        flowExpenseService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除报销")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        flowExpenseService.delete(id);
        return Result.success();
    }

    @Operation(summary = "报销审批")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:expense:approve')")
    public Result<Void> approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowExpenseService.approve(dto);
        return Result.success();
    }
}
