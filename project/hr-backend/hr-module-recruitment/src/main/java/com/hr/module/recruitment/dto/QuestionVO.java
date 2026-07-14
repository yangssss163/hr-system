package com.hr.module.recruitment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuestionVO {
    private Long id;
    private String category;
    private String categoryName;
    private String difficulty;
    private String difficultyName;
    private String title;
    private String answer;
    private LocalDateTime createTime;
}
