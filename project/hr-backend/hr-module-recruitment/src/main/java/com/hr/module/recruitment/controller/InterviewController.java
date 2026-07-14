package com.hr.module.recruitment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.*;
import com.hr.module.recruitment.service.RecInterviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "面试管理")
@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final RecInterviewService recInterviewService;

    @Operation(summary = "面试列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('recruitment:interview:list')")
    public Result<IPage<InterviewVO>> list(InterviewQuery query) {
        return Result.success(recInterviewService.page(query));
    }

    @Operation(summary = "面试详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:interview:list')")
    public Result<InterviewVO> getById(@PathVariable Long id) {
        return Result.success(recInterviewService.getById(id));
    }

    @Operation(summary = "安排面试")
    @PostMapping
    @PreAuthorize("hasAuthority('recruitment:interview:create')")
    public Result<Void> create(@Valid @RequestBody InterviewDTO dto) {
        recInterviewService.create(dto);
        return Result.success();
    }

    @Operation(summary = "更改面试时间")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:interview:edit')")
    public Result<Void> updateTime(@PathVariable Long id, @Valid @RequestBody InterviewDTO dto) {
        recInterviewService.updateTime(id, dto);
        return Result.success();
    }

    @Operation(summary = "面试签到")
    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasAuthority('recruitment:interview:list')")
    public Result<Void> checkin(@PathVariable Long id) {
        recInterviewService.checkin(id);
        return Result.success();
    }

    @Operation(summary = "面试评价")
    @PutMapping("/{id}/evaluate")
    @PreAuthorize("hasAuthority('recruitment:interview:evaluate')")
    public Result<Void> evaluate(@PathVariable Long id, @Valid @RequestBody InterviewEvaluateDTO dto) {
        recInterviewService.evaluate(id, dto);
        return Result.success();
    }

    @Operation(summary = "通过面试")
    @PutMapping("/{id}/pass")
    @PreAuthorize("hasAuthority('recruitment:interview:decide')")
    public Result<Void> pass(@PathVariable Long id) {
        recInterviewService.pass(id);
        return Result.success();
    }

    @Operation(summary = "淘汰")
    @PutMapping("/{id}/eliminate")
    @PreAuthorize("hasAuthority('recruitment:interview:decide')")
    public Result<Void> eliminate(@PathVariable Long id, @Valid @RequestBody InterviewEliminateDTO dto) {
        recInterviewService.eliminate(id, dto);
        return Result.success();
    }

    @Operation(summary = "发送 Offer")
    @PutMapping("/{id}/offer")
    @PreAuthorize("hasAuthority('recruitment:interview:offer')")
    public Result<Void> offer(@PathVariable Long id, @Valid @RequestBody InterviewOfferDTO dto) {
        recInterviewService.offer(id, dto);
        return Result.success();
    }

    @Operation(summary = "确认入职")
    @PutMapping("/{id}/confirm-hire")
    @PreAuthorize("hasAuthority('recruitment:interview:hire')")
    public Result<Void> confirmHire(@PathVariable Long id) {
        recInterviewService.confirmHire(id);
        return Result.success();
    }
}
