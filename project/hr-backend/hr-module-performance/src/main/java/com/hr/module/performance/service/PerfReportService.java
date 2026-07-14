package com.hr.module.performance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.performance.vo.PerfRecordVO;

import java.util.List;
import java.util.Map;

public interface PerfReportService {
    IPage<PerfRecordVO> detail(Long planId, Long deptId, Integer page, Integer pageSize);
    List<Map<String, Object>> deptSummary();
    IPage<PerfRecordVO> employeeSummary(Integer page, Integer pageSize);
}
