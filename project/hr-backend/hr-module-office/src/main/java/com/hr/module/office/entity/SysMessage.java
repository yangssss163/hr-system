package com.hr.module.office.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {
    private Long senderId;
    private Long receiverId;
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime sendTime;
}
