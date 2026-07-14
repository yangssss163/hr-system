package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmPaymentDTO;
import com.hr.module.crm.dto.CrmPaymentQuery;
import com.hr.module.crm.dto.CrmPaymentVO;
import com.hr.module.crm.service.CrmPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "付款管理")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CrmPaymentService crmPaymentService;

    @Operation(summary = "付款列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:payment:list')")
    public Result<PageResult<CrmPaymentVO>> list(@Valid CrmPaymentQuery query) {
        IPage<CrmPaymentVO> page = crmPaymentService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "付款详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:payment:list')")
    public Result<CrmPaymentVO> detail(@PathVariable Long id) {
        return Result.success(crmPaymentService.getById(id));
    }

    @Operation(summary = "新增付款")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:payment:create')")
    public Result<Void> create(@Valid @RequestBody CrmPaymentDTO dto) {
        crmPaymentService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑付款")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:payment:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmPaymentDTO dto) {
        crmPaymentService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除付款")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:payment:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmPaymentService.delete(id);
        return Result.success();
    }
}
