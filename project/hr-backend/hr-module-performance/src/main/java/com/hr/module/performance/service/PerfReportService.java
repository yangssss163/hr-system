package com.hr.module.performance.service;

import com.hr.common.result.PageResult;
import com.hr.module.performance.vo.PerfRecordVO;

import java.util.List;
import java.util.Map;

public interface PerfReportService {
    PageResult<PerfRecordVO> detail(Long planId, Long deptId, Long levelId, Integer page, Integer pageSize);
    List<Map<String, Object>> deptSummary(Long planId);
    PageResult<PerfRecordVO> employeeSummary(Long planId, Long deptId, Integer page, Integer pageSize);
}
