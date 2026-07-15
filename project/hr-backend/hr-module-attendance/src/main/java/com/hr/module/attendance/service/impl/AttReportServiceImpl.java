package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttRecordVO;
import com.hr.module.attendance.dto.AttReportDetailQuery;
import com.hr.module.attendance.dto.AttReportSummaryVO;
import com.hr.module.attendance.entity.AttRecord;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper.EmployeeInfo;
import com.hr.module.attendance.mapper.AttRecordMapper;
import com.hr.module.attendance.service.AttReportService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttReportServiceImpl implements AttReportService {

    private final AttRecordMapper attRecordMapper;
    private final AttendanceEmployeeHelper employeeHelper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public PageResult<AttRecordVO> detail(AttReportDetailQuery query) {
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttRecord::getEmployeeId, query.getEmployeeId());
        }
        if (query.getDeptId() != null) {
            // 根据部门ID过滤：先查询该部门下的所有员工ID
            List<HrEmployee> deptEmps = hrEmployeeMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrEmployee>()
                            .eq(HrEmployee::getDeptId, query.getDeptId())
                            .select(HrEmployee::getId));
            List<Long> deptEmpIds = deptEmps.stream().map(HrEmployee::getId).collect(Collectors.toList());
            if (deptEmpIds.isEmpty()) {
                wrapper.eq(AttRecord::getId, -1L); // 无匹配员工，返回空
            } else {
                wrapper.in(AttRecord::getEmployeeId, deptEmpIds);
            }
        }
        if (StringUtils.hasText(query.getDateStart())) {
            wrapper.ge(AttRecord::getRecordDate, LocalDate.parse(query.getDateStart()));
        }
        if (StringUtils.hasText(query.getDateEnd())) {
            wrapper.le(AttRecord::getRecordDate, LocalDate.parse(query.getDateEnd()));
        }
        wrapper.orderByDesc(AttRecord::getRecordDate);

        Page<AttRecord> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttRecord> result = attRecordMapper.selectPage(page, wrapper);

        List<AttRecordVO> voList = result.getRecords().stream().map(r -> {
            AttRecordVO vo = new AttRecordVO();
            vo.setId(r.getId());
            vo.setEmployeeId(r.getEmployeeId());
            vo.setRecordDate(r.getRecordDate());
            vo.setCheckIn(r.getCheckIn());
            vo.setCheckOut(r.getCheckOut());
            vo.setStatus(r.getStatus());
            vo.setSource(r.getSource());
            return vo;
        }).collect(Collectors.toList());
        // 填充员工信息
        Set<Long> empIds = result.getRecords().stream().map(AttRecord::getEmployeeId).collect(Collectors.toSet());
        Map<Long, EmployeeInfo> empMap = employeeHelper.getEmployeeInfoMap(empIds);
        voList.forEach(vo -> {
            EmployeeInfo info = empMap.get(vo.getEmployeeId());
            if (info != null) {
                vo.setEmpNo(info.getEmpNo());
                vo.setEmployeeName(info.getEmployeeName());
                vo.setDeptName(info.getDeptName());
            }
        });

        PageResult<AttRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(query.getPage());
        pageResult.setPageSize(query.getPageSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public List<AttReportSummaryVO> summary(String deptId, String month) {
        // 如果有 deptId 过滤，查询部门下所有员工
        List<Long> deptEmpIds = null;
        if (deptId != null && !deptId.isEmpty()) {
            List<HrEmployee> deptEmps = hrEmployeeMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrEmployee>()
                            .eq(HrEmployee::getDeptId, Long.parseLong(deptId))
                            .select(HrEmployee::getId));
            deptEmpIds = deptEmps.stream().map(HrEmployee::getId).collect(Collectors.toList());
            if (deptEmpIds.isEmpty()) {
                return new ArrayList<>();
            }
        }

        // 查询考勤记录
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        if (deptEmpIds != null) {
            wrapper.in(AttRecord::getEmployeeId, deptEmpIds);
        }
        if (month != null && !month.isEmpty()) {
            String dateStart = month + "-01";
            wrapper.ge(AttRecord::getRecordDate, LocalDate.parse(dateStart));
            wrapper.lt(AttRecord::getRecordDate, LocalDate.parse(dateStart).plusMonths(1));
        }
        List<AttRecord> records = attRecordMapper.selectList(wrapper);

        // 按员工聚合
        Map<Long, List<AttRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(AttRecord::getEmployeeId));
        Map<Long, EmployeeInfo> empMap = employeeHelper.getEmployeeInfoMap(grouped.keySet());

        List<AttReportSummaryVO> result = new ArrayList<>();
        grouped.forEach((empId, recs) -> {
            AttReportSummaryVO vo = new AttReportSummaryVO();
            vo.setEmployeeId(empId);
            EmployeeInfo info = empMap.get(empId);
            if (info != null) {
                vo.setEmpNo(info.getEmpNo());
                vo.setEmployeeName(info.getEmployeeName());
                vo.setDeptName(info.getDeptName());
            }
            vo.setLateCount((int) recs.stream().filter(r -> "late".equals(r.getStatus())).count());
            vo.setEarlyCount((int) recs.stream().filter(r -> "early".equals(r.getStatus())).count());
            vo.setAbsentCount((int) recs.stream().filter(r -> "absent".equals(r.getStatus())).count());
            vo.setOvertimeHours(recs.stream().filter(r -> "overtime".equals(r.getStatus())).count() * 1.0);
            result.add(vo);
        });
        return result;
    }
}
