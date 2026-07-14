package com.hr.module.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.attendance.dto.AttLeaveQuotaAdjustDTO;
import com.hr.module.attendance.dto.AttLeaveQuotaQuery;
import com.hr.module.attendance.dto.AttLeaveQuotaVO;

public interface AttLeaveQuotaService {
    IPage<AttLeaveQuotaVO> page(AttLeaveQuotaQuery query);
    void adjust(Long id, AttLeaveQuotaAdjustDTO dto);
}
