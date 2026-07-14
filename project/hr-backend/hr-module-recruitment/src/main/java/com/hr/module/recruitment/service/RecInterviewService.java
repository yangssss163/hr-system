package com.hr.module.recruitment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.recruitment.dto.*;

public interface RecInterviewService {
    IPage<InterviewVO> page(InterviewQuery query);
    InterviewVO getById(Long id);
    void create(InterviewDTO dto);
    void updateTime(Long id, InterviewDTO dto);
    void checkin(Long id);
    void evaluate(Long id, InterviewEvaluateDTO dto);
    void pass(Long id);
    void eliminate(Long id, InterviewEliminateDTO dto);
    void offer(Long id, InterviewOfferDTO dto);
    void confirmHire(Long id);
}
