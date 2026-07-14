package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmOrderDTO;
import com.hr.module.crm.dto.CrmOrderQuery;
import com.hr.module.crm.dto.CrmOrderVO;
import com.hr.module.crm.service.CrmOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CrmOrderService crmOrderService;

    @Operation(summary = "订单列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:order:list')")
    public Result<PageResult<CrmOrderVO>> list(@Valid CrmOrderQuery query) {
        IPage<CrmOrderVO> page = crmOrderService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:order:list')")
    public Result<CrmOrderVO> detail(@PathVariable Long id) {
        return Result.success(crmOrderService.getById(id));
    }

    @Operation(summary = "新增订单")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:order:create')")
    public Result<Void> create(@Valid @RequestBody CrmOrderDTO dto) {
        crmOrderService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑订单")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:order:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmOrderDTO dto) {
        crmOrderService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:order:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmOrderService.delete(id);
        return Result.success();
    }
}
