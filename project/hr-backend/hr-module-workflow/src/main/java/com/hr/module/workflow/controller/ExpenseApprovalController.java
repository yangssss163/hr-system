package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowExpenseDTO;
import com.hr.module.workflow.dto.FlowExpenseQuery;
import com.hr.module.workflow.dto.FlowExpenseVO;
import com.hr.module.workflow.service.FlowExpenseService;
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

    @GetMapping
    @PreAuthorize("hasAuthority('workflow:expense:list')")
    public IPage<FlowExpenseVO> list(FlowExpenseQuery query) {
        return flowExpenseService.page(query);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:list')")
    public FlowExpenseVO detail(@PathVariable Long id) {
        return flowExpenseService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('workflow:expense:create')")
    public void create(@Valid @RequestBody FlowExpenseDTO dto) {
        flowExpenseService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:update')")
    public void update(@PathVariable Long id, @Valid @RequestBody FlowExpenseDTO dto) {
        flowExpenseService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:expense:delete')")
    public void delete(@PathVariable Long id) {
        flowExpenseService.delete(id);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:expense:approve')")
    public void approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowExpenseService.approve(dto);
    }
}
