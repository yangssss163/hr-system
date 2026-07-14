package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmCustomerDTO;
import com.hr.module.crm.dto.CrmCustomerQuery;
import com.hr.module.crm.dto.CrmCustomerVO;
import com.hr.module.crm.service.CrmCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客户管理")
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CrmCustomerService crmCustomerService;

    @Operation(summary = "客户列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:customer:list')")
    public Result<PageResult<CrmCustomerVO>> list(@Valid CrmCustomerQuery query) {
        IPage<CrmCustomerVO> page = crmCustomerService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "客户详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:customer:list')")
    public Result<CrmCustomerVO> detail(@PathVariable Long id) {
        return Result.success(crmCustomerService.getById(id));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:customer:create')")
    public Result<Void> create(@Valid @RequestBody CrmCustomerDTO dto) {
        crmCustomerService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑客户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:customer:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmCustomerDTO dto) {
        crmCustomerService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:customer:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmCustomerService.delete(id);
        return Result.success();
    }
}
