package com.hr.module.recruitment.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("rec_question")
public class RecQuestion extends BaseEntity {
    private String category;
    private String difficulty;
    private String title;
    private String answer;
}
