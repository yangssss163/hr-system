package com.hr.module.system.controller;

import com.hr.common.result.Result;
import com.hr.module.system.dto.PositionVO;
import com.hr.module.system.entity.SysPosition;
import com.hr.module.system.service.SysPositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "职位管理")
@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class SysPositionController {

    private final SysPositionService sysPositionService;

    @Operation(summary = "职位列表（分页）")
    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String keyword) {
        List<PositionVO> records = sysPositionService.list(page, pageSize, deptId, keyword);
        Long total = sysPositionService.count(page, pageSize, deptId, keyword);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("records", records);
        return Result.success(result);
    }

    @Operation(summary = "获取职位详情")
    @GetMapping("/{id}")
    public Result<SysPosition> getById(@PathVariable Long id) {
        return Result.success(sysPositionService.getById(id));
    }

    @Operation(summary = "创建职位")
    @PostMapping
    @PreAuthorize("hasAuthority('system:position:create')")
    public Result<Void> create(@RequestBody SysPosition position) {
        sysPositionService.create(position);
        return Result.success();
    }

    @Operation(summary = "编辑职位")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:position:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysPosition position) {
        position.setId(id);
        sysPositionService.update(position);
        return Result.success();
    }

    @Operation(summary = "删除职位")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:position:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysPositionService.delete(id);
        return Result.success();
    }
}
