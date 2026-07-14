package com.hr.module.office.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysMessageDTO {
    @NotNull(message = "发送人不能为空")
    private Long senderId;
    @NotNull(message = "接收人不能为空")
    private Long receiverId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String content;
    private Integer isRead;
    private LocalDateTime sendTime;
}
