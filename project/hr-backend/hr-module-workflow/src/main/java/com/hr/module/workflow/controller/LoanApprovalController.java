package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLoanDTO;
import com.hr.module.workflow.dto.FlowLoanQuery;
import com.hr.module.workflow.dto.FlowLoanVO;
import com.hr.module.workflow.service.FlowLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "借款审批")
@RestController
@RequestMapping("/api/workflow/loans")
@RequiredArgsConstructor
public class LoanApprovalController {

    private final FlowLoanService flowLoanService;

    @Operation(summary = "借款列表")
    @GetMapping
    @PreAuthorize("hasAuthority('workflow:loan:list')")
    public Result<IPage<FlowLoanVO>> list(FlowLoanQuery query) {
        return Result.success(flowLoanService.page(query));
    }

    @Operation(summary = "借款详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:list')")
    public Result<FlowLoanVO> detail(@PathVariable Long id) {
        return Result.success(flowLoanService.getById(id));
    }

    @Operation(summary = "申请借款")
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:loan:create')")
    public Result<Void> create(@Valid @RequestBody FlowLoanDTO dto) {
        flowLoanService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑借款")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FlowLoanDTO dto) {
        flowLoanService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除借款")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        flowLoanService.delete(id);
        return Result.success();
    }

    @Operation(summary = "借款审批")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:loan:approve')")
    public Result<Void> approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowLoanService.approve(dto);
        return Result.success();
    }
}
