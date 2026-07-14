package com.hr.module.recruitment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.recruitment.dto.*;

import java.util.List;

public interface RecResumeService {
    IPage<ResumeVO> page(ResumeQuery query);
    ResumeVO getById(Long id);
    void create(ResumeDTO dto);
    void update(Long id, ResumeDTO dto);
    void delete(Long id);
    void batchDelete(List<Long> ids);
    void invite(Long id, InterviewInviteDTO dto);
}
