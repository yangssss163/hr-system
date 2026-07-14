package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysMessageQuery {
    private String keyword;
    private Integer isRead;
    private Long receiverId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
