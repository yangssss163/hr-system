package com.hr.module.recruitment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.BlacklistDTO;
import com.hr.module.recruitment.dto.BlacklistVO;
import com.hr.module.recruitment.service.RecBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "面试黑名单")
@RestController
@RequestMapping("/api/blacklists")
@RequiredArgsConstructor
public class BlacklistController {

    private final RecBlacklistService recBlacklistService;

    @Operation(summary = "黑名单列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('recruitment:blacklist:list')")
    public Result<IPage<BlacklistVO>> list(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        return Result.success(recBlacklistService.page(page, pageSize, keyword));
    }

    @Operation(summary = "黑名单详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:blacklist:list')")
    public Result<BlacklistVO> detail(@PathVariable Long id) {
        return Result.success(recBlacklistService.getById(id));
    }

    @Operation(summary = "加入黑名单")
    @PostMapping
    @PreAuthorize("hasAuthority('recruitment:blacklist:create')")
    public Result<Void> create(@Valid @RequestBody BlacklistDTO dto) {
        recBlacklistService.create(dto);
        return Result.success();
    }

    @Operation(summary = "移出黑名单")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:blacklist:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        recBlacklistService.delete(id);
        return Result.success();
    }
}
