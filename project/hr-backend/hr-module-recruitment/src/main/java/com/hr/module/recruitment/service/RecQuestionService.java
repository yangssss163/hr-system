package com.hr.module.recruitment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.recruitment.dto.QuestionDTO;
import com.hr.module.recruitment.dto.QuestionQuery;
import com.hr.module.recruitment.dto.QuestionVO;

public interface RecQuestionService {
    IPage<QuestionVO> page(QuestionQuery query);
    QuestionVO getById(Long id);
    void create(QuestionDTO dto);
    void update(Long id, QuestionDTO dto);
    void delete(Long id);
}
