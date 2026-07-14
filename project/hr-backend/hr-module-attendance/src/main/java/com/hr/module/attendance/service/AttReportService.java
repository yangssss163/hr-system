package com.hr.module.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;

import java.util.List;

public interface AttReportService {
    IPage<AttReportSummaryVO> detail(AttReportDetailQuery query);
    List<AttReportSummaryVO> summary(String deptId, String month);
}
