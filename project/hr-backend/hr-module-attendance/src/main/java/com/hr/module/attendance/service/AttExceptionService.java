package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttExceptionQuery;
import com.hr.module.attendance.dto.AttExceptionVO;

public interface AttExceptionService {
    PageResult<AttExceptionVO> page(AttExceptionQuery query);
}
