package com.hr.module.recruitment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlacklistVO {
    private Long id;
    private String name;
    private String phone;
    private String idCard;
    private String reason;
    private LocalDateTime createTime;
}
