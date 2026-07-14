package com.hr.module.office.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_task")
public class SysTask extends BaseEntity {
    private Long creatorId;
    private Long assigneeId;
    private String title;
    private String content;
    private String priority;
    private String status;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime completeTime;
}
