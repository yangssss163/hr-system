package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLeaveDTO;
import com.hr.module.workflow.dto.FlowLeaveQuery;
import com.hr.module.workflow.dto.FlowLeaveVO;
import com.hr.module.workflow.service.FlowLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "请假审批")
@RestController
@RequestMapping("/api/workflow/leaves")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final FlowLeaveService flowLeaveService;

    @Operation(summary = "请假列表")
    @GetMapping
    @PreAuthorize("hasAuthority('workflow:leave:list')")
    public Result<IPage<FlowLeaveVO>> list(FlowLeaveQuery query) {
        return Result.success(flowLeaveService.page(query));
    }

    @Operation(summary = "请假详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:list')")
    public Result<FlowLeaveVO> detail(@PathVariable Long id) {
        return Result.success(flowLeaveService.getById(id));
    }

    @Operation(summary = "申请请假")
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:leave:create')")
    public Result<Void> create(@Valid @RequestBody FlowLeaveDTO dto) {
        flowLeaveService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑请假")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FlowLeaveDTO dto) {
        flowLeaveService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除请假")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        flowLeaveService.delete(id);
        return Result.success();
    }

    @Operation(summary = "请假审批")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:leave:approve')")
    public Result<Void> approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowLeaveService.approve(dto);
        return Result.success();
    }
}
