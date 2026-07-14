package com.hr.module.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowTravelDTO;
import com.hr.module.workflow.dto.FlowTravelQuery;
import com.hr.module.workflow.dto.FlowTravelVO;
import com.hr.module.workflow.service.FlowTravelService;
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

    @GetMapping
    @PreAuthorize("hasAuthority('workflow:travel:list')")
    public IPage<FlowTravelVO> list(FlowTravelQuery query) {
        return flowTravelService.page(query);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:list')")
    public FlowTravelVO detail(@PathVariable Long id) {
        return flowTravelService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('workflow:travel:create')")
    public void create(@Valid @RequestBody FlowTravelDTO dto) {
        flowTravelService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:update')")
    public void update(@PathVariable Long id, @Valid @RequestBody FlowTravelDTO dto) {
        flowTravelService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('workflow:travel:delete')")
    public void delete(@PathVariable Long id) {
        flowTravelService.delete(id);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('workflow:travel:approve')")
    public void approve(@PathVariable Long id, @Valid @RequestBody FlowApproveDTO dto) {
        dto.setId(id);
        flowTravelService.approve(dto);
    }
}
