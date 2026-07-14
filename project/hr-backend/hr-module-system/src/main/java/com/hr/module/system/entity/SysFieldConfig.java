package com.hr.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_field_config")
public class SysFieldConfig implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String module;
    private String fieldKey;
    private String fieldName;
    private Integer visible;
    private Integer required;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
