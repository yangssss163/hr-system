package com.hr.module.office.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.office.dto.SysNoticeDTO;
import com.hr.module.office.dto.SysNoticeQuery;
import com.hr.module.office.dto.SysNoticeVO;

public interface SysNoticeService {
    IPage<SysNoticeVO> page(SysNoticeQuery query);
    SysNoticeVO getById(Long id);
    void create(SysNoticeDTO dto);
    void update(Long id, SysNoticeDTO dto);
    void delete(Long id);
}
