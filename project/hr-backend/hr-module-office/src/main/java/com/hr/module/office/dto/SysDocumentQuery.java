package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysDocumentQuery {
    private String keyword;
    private String category;
    private Integer isPublic;
    private Integer page = 1;
    private Integer pageSize = 10;
}
