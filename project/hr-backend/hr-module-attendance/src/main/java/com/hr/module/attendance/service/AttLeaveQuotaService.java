package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttLeaveQuotaAdjustDTO;
import com.hr.module.attendance.dto.AttLeaveQuotaQuery;
import com.hr.module.attendance.dto.AttLeaveQuotaVO;

public interface AttLeaveQuotaService {
    PageResult<AttLeaveQuotaVO> page(AttLeaveQuotaQuery query);
    void adjust(Long id, AttLeaveQuotaAdjustDTO dto);
}
