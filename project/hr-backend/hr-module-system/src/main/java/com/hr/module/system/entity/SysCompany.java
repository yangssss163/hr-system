package com.hr.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_company")
public class SysCompany extends BaseEntity {
    private String name;
    private String code;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
