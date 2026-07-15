package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;

import java.util.List;

public interface AttReportService {
    PageResult<AttReportSummaryVO> detail(AttReportDetailQuery query);
    List<AttReportSummaryVO> summary(String deptId, String month);
}
