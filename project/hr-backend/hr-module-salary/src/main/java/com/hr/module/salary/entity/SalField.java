package com.hr.module.salary.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sal_field")
public class SalField extends BaseEntity {
    private String name;
    private String code;
    private String type;
    private String formula;
    private Integer sort;
    private Integer status;
}
