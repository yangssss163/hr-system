package com.hr.module.recruitment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.recruitment.dto.NotifyTemplateDTO;
import com.hr.module.recruitment.dto.NotifyTemplateVO;

public interface RecNotifyTemplateService {
    IPage<NotifyTemplateVO> page(Integer page, Integer pageSize);
    NotifyTemplateVO getById(Long id);
    void create(NotifyTemplateDTO dto);
    void update(Long id, NotifyTemplateDTO dto);
    void delete(Long id);
}
