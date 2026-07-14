package com.hr.module.office.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_document")
public class SysDocument extends BaseEntity {
    private String title;
    private String content;
    private String category;
    private Long parentId;
    private Long creatorId;
    private Integer isPublic;
    private String fileUrl;
}
