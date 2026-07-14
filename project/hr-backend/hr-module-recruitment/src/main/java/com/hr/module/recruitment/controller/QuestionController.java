package com.hr.module.recruitment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.module.recruitment.dto.QuestionDTO;
import com.hr.module.recruitment.dto.QuestionQuery;
import com.hr.module.recruitment.dto.QuestionVO;
import com.hr.module.recruitment.service.RecQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "面试题库")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final RecQuestionService recQuestionService;

    @Operation(summary = "题库列表（分页）")
    @GetMapping
    @PreAuthorize("hasAuthority('recruitment:question:list')")
    public Result<IPage<QuestionVO>> list(QuestionQuery query) {
        return Result.success(recQuestionService.page(query));
    }

    @Operation(summary = "试题详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:question:list')")
    public Result<QuestionVO> getById(@PathVariable Long id) {
        return Result.success(recQuestionService.getById(id));
    }

    @Operation(summary = "创建试题")
    @PostMapping
    @PreAuthorize("hasAuthority('recruitment:question:create')")
    public Result<Void> create(@Valid @RequestBody QuestionDTO dto) {
        recQuestionService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑试题")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:question:edit')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody QuestionDTO dto) {
        recQuestionService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除试题")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('recruitment:question:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        recQuestionService.delete(id);
        return Result.success();
    }
}
