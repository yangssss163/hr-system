package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttExceptionQuery;
import com.hr.module.attendance.dto.AttExceptionVO;
import com.hr.module.attendance.entity.AttRecord;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper.EmployeeInfo;
import com.hr.module.attendance.mapper.AttRecordMapper;
import com.hr.module.attendance.service.AttExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttExceptionServiceImpl implements AttExceptionService {

    private static final Map<String, String> TYPE_NAME_MAP = new HashMap<>();

    static {
        TYPE_NAME_MAP.put("late", "迟到");
        TYPE_NAME_MAP.put("early", "早退");
        TYPE_NAME_MAP.put("absent", "缺勤");
        TYPE_NAME_MAP.put("overtime", "加班");
    }

    private final AttRecordMapper attRecordMapper;
    private final AttendanceEmployeeHelper employeeHelper;

    @Override
    public PageResult<AttExceptionVO> page(AttExceptionQuery query) {
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(AttRecord::getStatus, query.getType());
        } else {
            wrapper.ne(AttRecord::getStatus, "normal");
        }
        if (StringUtils.hasText(query.getDate())) {
            wrapper.eq(AttRecord::getRecordDate, LocalDate.parse(query.getDate()));
        } else {
            if (StringUtils.hasText(query.getDateStart())) {
                wrapper.ge(AttRecord::getRecordDate, LocalDate.parse(query.getDateStart()));
            }
            if (StringUtils.hasText(query.getDateEnd())) {
                wrapper.le(AttRecord::getRecordDate, LocalDate.parse(query.getDateEnd()));
            }
        }
        wrapper.orderByDesc(AttRecord::getRecordDate);

        Page<AttRecord> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttRecord> result = attRecordMapper.selectPage(page, wrapper);

        List<AttExceptionVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
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
        PageResult<AttExceptionVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    private AttExceptionVO toVO(AttRecord entity) {
        AttExceptionVO vo = new AttExceptionVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setRecordDate(entity.getRecordDate());
        vo.setType(entity.getStatus());
        vo.setTypeName(TYPE_NAME_MAP.getOrDefault(entity.getStatus(), entity.getStatus()));
        vo.setDetail("");
        vo.setOaStatus("");
        return vo;
    }
}
