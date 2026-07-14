package com.hr.module.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.attendance.dto.AttExceptionQuery;
import com.hr.module.attendance.dto.AttExceptionVO;

public interface AttExceptionService {
    IPage<AttExceptionVO> page(AttExceptionQuery query);
}
