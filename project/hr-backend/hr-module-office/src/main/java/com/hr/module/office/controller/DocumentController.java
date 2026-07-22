package com.hr.module.office.controller;

import com.hr.common.result.Result;
import com.hr.framework.util.MinioUtils;
import com.hr.module.office.dto.SysDocumentDTO;
import com.hr.module.office.dto.SysDocumentQuery;
import com.hr.module.office.dto.SysDocumentVO;
import com.hr.module.office.service.SysDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "文档管理")
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final SysDocumentService sysDocumentService;
    private final MinioUtils minioUtils;

    @Operation(summary = "上传文件并创建文档")
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('office:document:create')")
    public Result<SysDocumentVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "creatorId", required = false) Long creatorId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "0") Integer isPublic) {
        try {
            Map<String, Object> uploadResult = minioUtils.upload(file, "document");
            SysDocumentDTO dto = new SysDocumentDTO();
            dto.setTitle(title);
            dto.setCategory(category);
            dto.setCreatorId(creatorId);
            dto.setIsPublic(isPublic);
            dto.setFileUrl((String) uploadResult.get("fileUrl"));
            sysDocumentService.create(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @Operation(summary = "列表")
    @GetMapping
    @PreAuthorize("hasAuthority('office:document:list')")
    public Result<?> list(SysDocumentQuery query) {
        return Result.success(sysDocumentService.page(query));
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('office:document:list')")
    public Result<SysDocumentVO> detail(@PathVariable Long id) {
        return Result.success(sysDocumentService.getById(id));
    }

    @Operation(summary = "创建")
    @PostMapping
    @PreAuthorize("hasAuthority('office:document:create')")
    public Result<Void> create(@Valid @RequestBody SysDocumentDTO dto) {
        sysDocumentService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('office:document:update')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysDocumentDTO dto) {
        sysDocumentService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('office:document:delete')")
    public Result<Void> delete(@PathVariable Long id) {
        sysDocumentService.delete(id);
        return Result.success();
    }
}
