package com.hr.module.employee.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("hr_transfer")
public class HrTransfer {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;
    private String transferType;
    private Long beforeDeptId;
    private Long afterDeptId;
    private Long beforePositionId;
    private Long afterPositionId;
    private LocalDate effectiveDate;
    private String reason;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
