package com.hr.module.recruitment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rec_blacklist")
public class RecBlacklist {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resumeId;
    private String name;
    private String phone;
    private String idCard;
    private String reason;
    private LocalDateTime createTime;
}
