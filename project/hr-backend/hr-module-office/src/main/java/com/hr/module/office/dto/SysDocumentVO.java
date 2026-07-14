package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysDocumentVO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long parentId;
    private Long creatorId;
    private String creatorName;
    private Integer isPublic;
    private String fileUrl;
    private LocalDateTime createTime;
}
