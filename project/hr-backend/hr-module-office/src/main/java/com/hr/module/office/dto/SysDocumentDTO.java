package com.hr.module.office.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SysDocumentDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    private String category;
    private Long parentId;
    private Long creatorId;
    private Integer isPublic;
    private String fileUrl;
}
