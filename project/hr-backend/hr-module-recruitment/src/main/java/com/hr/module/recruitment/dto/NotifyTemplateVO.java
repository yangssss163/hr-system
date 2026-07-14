package com.hr.module.recruitment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotifyTemplateVO {
    private Long id;
    private String name;
    private String type;
    private String typeName;
    private String title;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}
