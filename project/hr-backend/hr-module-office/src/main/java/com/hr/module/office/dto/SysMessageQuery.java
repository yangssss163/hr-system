package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysMessageQuery {
    private String keyword;
    private Integer isRead;
    private Long userId;
    private Long receiverId;
    private Long senderId;
    private Integer page = 1;
    private Integer pageSize = 10;
}
