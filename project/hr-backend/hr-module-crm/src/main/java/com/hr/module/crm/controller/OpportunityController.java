package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmOpportunityDTO;
import com.hr.module.crm.dto.CrmOpportunityQuery;
import com.hr.module.crm.dto.CrmOpportunityVO;
import com.hr.module.crm.service.CrmOpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商机管理")
@RestController
@RequestMapping("/api/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final CrmOpportunityService crmOpportunityService;

    @Operation(summary = "商机列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:opportunity:list')")
    public Result<PageResult<CrmOpportunityVO>> list(@Valid CrmOpportunityQuery query) {
        IPage<CrmOpportunityVO> page = crmOpportunityService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "商机详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:opportunity:list')")
    public Result<CrmOpportunityVO> detail(@PathVariable Long id) {
        return Result.success(crmOpportunityService.getById(id));
    }

    @Operation(summary = "新增商机")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:opportunity:create')")
    public Result<Void> create(@Valid @RequestBody CrmOpportunityDTO dto) {
        crmOpportunityService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑商机")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:opportunity:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmOpportunityDTO dto) {
        crmOpportunityService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除商机")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:opportunity:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmOpportunityService.delete(id);
        return Result.success();
    }
}
