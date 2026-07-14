package com.hr.module.office.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.office.dto.SysMessageDTO;
import com.hr.module.office.dto.SysMessageQuery;
import com.hr.module.office.dto.SysMessageVO;

public interface SysMessageService {
    IPage<SysMessageVO> page(SysMessageQuery query);
    SysMessageVO getById(Long id);
    void create(SysMessageDTO dto);
    void update(Long id, SysMessageDTO dto);
    void delete(Long id);
}
