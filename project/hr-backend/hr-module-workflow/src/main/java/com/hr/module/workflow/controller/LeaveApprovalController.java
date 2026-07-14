package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLeaveDTO;
import com.hr.module.workflow.dto.FlowLeaveQuery;
import com.hr.module.workflow.dto.FlowLeaveVO;
import com.hr.module.workflow.service.FlowLeaveService;
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

    @GetMapping
    @PreAuthorize("hasAuthority('workflow:leave:list')")
    public IPage<FlowLeaveVO> list(FlowLeaveQuery query) {
        return flowLeaveService.page(query);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:list')")
    public FlowLeaveVO detail(@PathVariable Long id) {
        return flowLeaveService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('workflow:leave:create')")
    public void create(@Valid @RequestBody FlowLeaveDTO dto) {
        flowLeaveService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:update')")
    public void update(@PathVariable Long id, @Valid @RequestBody FlowLeaveDTO dto) {
        flowLeaveService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:leave:delete')")
    public void delete(@PathVariable Long id) {
        flowLeaveService.delete(id);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:leave:approve')")
    public void approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowLeaveService.approve(dto);
    }
}
