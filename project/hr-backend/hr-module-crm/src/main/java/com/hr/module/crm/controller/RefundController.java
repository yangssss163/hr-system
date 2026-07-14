package com.hr.module.crm.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.PageResult;
import com.hr.common.result.Result;
import com.hr.module.crm.dto.CrmRefundDTO;
import com.hr.module.crm.dto.CrmRefundQuery;
import com.hr.module.crm.dto.CrmRefundVO;
import com.hr.module.crm.service.CrmRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "退款管理")
@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final CrmRefundService crmRefundService;

    @Operation(summary = "退款列表")
    @GetMapping
    @PreAuthorize("hasAuthority('crm:refund:list')")
    public Result<PageResult<CrmRefundVO>> list(@Valid CrmRefundQuery query) {
        IPage<CrmRefundVO> page = crmRefundService.page(query);
        return Result.success(PageResult.of(page));
    }

    @Operation(summary = "退款详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:refund:list')")
    public Result<CrmRefundVO> detail(@PathVariable Long id) {
        return Result.success(crmRefundService.getById(id));
    }

    @Operation(summary = "新增退款")
    @PostMapping
    @PreAuthorize("hasAuthority('crm:refund:create')")
    public Result<Void> create(@Valid @RequestBody CrmRefundDTO dto) {
        crmRefundService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑退款")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:refund:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CrmRefundDTO dto) {
        crmRefundService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除退款")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('crm:refund:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        crmRefundService.delete(id);
        return Result.success();
    }
}
