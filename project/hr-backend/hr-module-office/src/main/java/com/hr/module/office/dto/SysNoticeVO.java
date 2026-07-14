package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysNoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Long publisherId;
    private String publisherName;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
}
