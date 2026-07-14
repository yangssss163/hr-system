package com.hr.module.employee.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.employee.dto.TransferDTO;
import com.hr.module.employee.dto.TransferQuery;
import com.hr.module.employee.dto.TransferVO;
import com.hr.module.employee.service.HrTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "异动管理")
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final HrTransferService hrTransferService;

    @Operation(summary = "异动列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('employee:transfer:list')")
    public Result<IPage<TransferVO>> list(TransferQuery query) {
        return Result.success(hrTransferService.page(query));
    }

    @Operation(summary = "异动详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:transfer:list')")
    public Result<TransferVO> getById(@PathVariable Long id) {
        return Result.success(hrTransferService.getById(id));
    }

    @Operation(summary = "创建异动")
    @PostMapping
    @PreAuthorize("hasAuthority('employee:transfer:create')")
    public Result<Void> create(@Valid @RequestBody TransferDTO dto) {
        hrTransferService.create(dto);
        return Result.success();
    }

    @Operation(summary = "撤销异动")
    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasAuthority('employee:transfer:revoke')")
    public Result<Void> revoke(@PathVariable Long id) {
        hrTransferService.revoke(id);
        return Result.success();
    }
}
