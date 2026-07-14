package com.hr.module.office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysNoticeDTO {
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    @NotNull(message = "类型不能为空")
    private Integer type;
    private Integer status;
    private Long publisherId;
    private LocalDateTime publishTime;
}
