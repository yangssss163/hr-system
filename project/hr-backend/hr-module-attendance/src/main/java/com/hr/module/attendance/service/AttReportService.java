package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttRecordVO;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;

import java.util.List;

public interface AttReportService {
    PageResult<AttRecordVO> detail(AttReportDetailQuery query);
    List<AttReportSummaryVO> summary(String deptId, String month);
}
