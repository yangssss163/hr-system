package com.hr.module.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_resume")
public class RecResume extends BaseEntity {
    private String name;
    private String phone;
    private String email;
    private Integer gender;
    private String education;
    private String school;
    private String major;
    private Integer workYears;
    private String applyPosition;
    private String source;
    private String status;
    private String resumeFile;
}
