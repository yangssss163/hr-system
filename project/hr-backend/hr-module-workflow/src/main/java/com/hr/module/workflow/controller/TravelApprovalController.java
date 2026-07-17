package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowTravelDTO;
import com.hr.module.workflow.dto.FlowTravelQuery;
import com.hr.module.workflow.dto.FlowTravelVO;
import com.hr.module.workflow.service.FlowTravelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "出差审批")
@RestController
@RequestMapping("/api/workflow/travels")
@RequiredArgsConstructor
public class TravelApprovalController {

    private final FlowTravelService flowTravelService;

    @Operation(summary = "出差列表")
    @GetMapping
    @PreAuthorize("hasAuthority('workflow:travel:list')")
    public Result<IPage<FlowTravelVO>> list(FlowTravelQuery query) {
        return Result.success(flowTravelService.page(query));
    }

    @Operation(summary = "出差详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:list')")
    public Result<FlowTravelVO> detail(@PathVariable Long id) {
        return Result.success(flowTravelService.getById(id));
    }

    @Operation(summary = "申请出差")
    @PostMapping
    @PreAuthorize("hasAuthority('workflow:travel:create')")
    public Result<Void> create(@Valid @RequestBody FlowTravelDTO dto) {
        flowTravelService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑出差")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FlowTravelDTO dto) {
        flowTravelService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除出差")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        flowTravelService.delete(id);
        return Result.success();
    }

    @Operation(summary = "出差审批")
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:travel:approve')")
    public Result<Void> approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowTravelService.approve(dto);
        return Result.success();
    }
}
