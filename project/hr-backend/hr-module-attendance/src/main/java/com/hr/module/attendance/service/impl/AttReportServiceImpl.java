package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;
import com.hr.module.attendance.entity.AttRecord;
import com.hr.module.attendance.mapper.AttRecordMapper;
import com.hr.module.attendance.service.AttReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttReportServiceImpl implements AttReportService {

    private final AttRecordMapper attRecordMapper;

    @Override
    public IPage<AttReportSummaryVO> detail(AttReportDetailQuery query) {
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttRecord::getEmployeeId, query.getEmployeeId());
        }
        if (StringUtils.hasText(query.getDateStart())) {
            wrapper.ge(AttRecord::getRecordDate, LocalDate.parse(query.getDateStart()));
        }
        if (StringUtils.hasText(query.getDateEnd())) {
            wrapper.le(AttRecord::getRecordDate, LocalDate.parse(query.getDateEnd()));
        }

        List<AttRecord> allRecords = attRecordMapper.selectList(wrapper);
        List<Long> employeeIds = allRecords.stream()
                .map(AttRecord::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());

        int total = employeeIds.size();
        int fromIndex = (query.getPage() - 1) * query.getPageSize();
        int toIndex = Math.min(fromIndex + query.getPageSize(), total);
        List<Long> pagedEmployeeIds = fromIndex < total
                ? employeeIds.subList(fromIndex, toIndex)
                : Collections.emptyList();

        List<AttReportSummaryVO> voList = pagedEmployeeIds.stream().map(empId -> {
            AttReportSummaryVO vo = new AttReportSummaryVO();
            vo.setEmployeeId(empId);
            vo.setShouldWorkDays(0);
            vo.setActualWorkDays(0);
            vo.setLateCount(0);
            vo.setEarlyCount(0);
            vo.setAbsentCount(0);
            vo.setLeaveCount(0);
            vo.setOvertimeHours(0.0);
            return vo;
        }).collect(Collectors.toList());

        Page<AttReportSummaryVO> voPage = new Page<>(query.getPage(), query.getPageSize(), total);
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<AttReportSummaryVO> summary(String deptId, String month) {
        return new ArrayList<>();
    }
}
