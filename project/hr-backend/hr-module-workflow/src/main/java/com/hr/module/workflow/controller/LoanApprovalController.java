package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLoanDTO;
import com.hr.module.workflow.dto.FlowLoanQuery;
import com.hr.module.workflow.dto.FlowLoanVO;
import com.hr.module.workflow.service.FlowLoanService;
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

    @GetMapping
    @PreAuthorize("hasAuthority('workflow:loan:list')")
    public IPage<FlowLoanVO> list(FlowLoanQuery query) {
        return flowLoanService.page(query);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:list')")
    public FlowLoanVO detail(@PathVariable Long id) {
        return flowLoanService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('workflow:loan:create')")
    public void create(@Valid @RequestBody FlowLoanDTO dto) {
        flowLoanService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:update')")
    public void update(@PathVariable Long id, @Valid @RequestBody FlowLoanDTO dto) {
        flowLoanService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:loan:delete')")
    public void delete(@PathVariable Long id) {
        flowLoanService.delete(id);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:loan:approve')")
    public void approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowLoanService.approve(dto);
    }
}
