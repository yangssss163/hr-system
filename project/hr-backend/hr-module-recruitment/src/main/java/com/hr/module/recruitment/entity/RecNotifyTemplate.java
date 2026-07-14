package com.hr.module.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_notify_template")
public class RecNotifyTemplate extends BaseEntity {
    private String name;
    private String type;
    private String title;
    private String content;
    private Integer status;
}
