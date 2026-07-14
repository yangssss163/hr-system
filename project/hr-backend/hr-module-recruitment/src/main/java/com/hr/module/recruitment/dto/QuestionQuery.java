package com.hr.module.recruitment.dto;

import lombok.Data;

@Data
public class QuestionQuery {
    private String keyword;
    private String category;
    private String difficulty;
    private Integer page = 1;
    private Integer pageSize = 10;
}
