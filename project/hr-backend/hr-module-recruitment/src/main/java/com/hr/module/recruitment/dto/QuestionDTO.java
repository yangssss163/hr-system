package com.hr.module.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuestionDTO {
    @NotBlank(message = "分类不能为空")
    private String category;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    @NotBlank(message = "题目不能为空")
    private String title;

    private String answer;
}
