package com.hr.module.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.attendance.dto.AttOaFlowQuery;
import com.hr.module.attendance.dto.AttOaFlowVO;

public interface AttOaFlowService {
    IPage<AttOaFlowVO> page(AttOaFlowQuery query);
    AttOaFlowVO getById(Long id);
}
