package com.hr.module.employee.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hr_employee")
public class HrEmployee extends BaseEntity {
    private Long userId;
    private String empNo;
    private String name;
    private Integer gender;
    private String idCard;
    private LocalDate birthday;
    private String phone;
    private String email;
    private Long deptId;
    private Long positionId;
    private Long companyId;
    private LocalDate entryDate;
    private Integer status;
}
