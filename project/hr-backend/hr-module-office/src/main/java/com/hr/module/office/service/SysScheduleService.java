package com.hr.module.office.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.office.dto.SysScheduleDTO;
import com.hr.module.office.dto.SysScheduleQuery;
import com.hr.module.office.dto.SysScheduleVO;

public interface SysScheduleService {
    IPage<SysScheduleVO> page(SysScheduleQuery query);
    SysScheduleVO getById(Long id);
    void create(SysScheduleDTO dto);
    void update(Long id, SysScheduleDTO dto);
    void delete(Long id);
}
