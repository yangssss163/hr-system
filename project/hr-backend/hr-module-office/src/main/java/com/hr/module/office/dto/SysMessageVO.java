package com.hr.module.office.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysMessageVO {
    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime sendTime;
    private LocalDateTime createTime;
}
