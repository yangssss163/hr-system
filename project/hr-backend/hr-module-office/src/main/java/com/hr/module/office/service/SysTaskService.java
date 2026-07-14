package com.hr.module.office.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.office.dto.SysTaskDTO;
import com.hr.module.office.dto.SysTaskQuery;
import com.hr.module.office.dto.SysTaskVO;

public interface SysTaskService {
    IPage<SysTaskVO> page(SysTaskQuery query);
    SysTaskVO getById(Long id);
    void create(SysTaskDTO dto);
    void update(Long id, SysTaskDTO dto);
    void delete(Long id);
}
