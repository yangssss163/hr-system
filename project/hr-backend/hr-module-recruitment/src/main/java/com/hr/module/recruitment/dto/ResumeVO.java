package com.hr.module.recruitment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResumeVO {
    private Long id;
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
    private LocalDateTime createTime;
}
