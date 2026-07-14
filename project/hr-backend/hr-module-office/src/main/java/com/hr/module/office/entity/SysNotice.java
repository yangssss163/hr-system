package com.hr.module.office.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notice")
public class SysNotice extends BaseEntity {
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private Long publisherId;
    private LocalDateTime publishTime;
}
