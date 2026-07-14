package com.hr.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dept")
public class SysDept extends BaseEntity {
    private Long companyId;
    private String name;
    private Long parentId;
    private Integer sort;
    private Integer status;
}
