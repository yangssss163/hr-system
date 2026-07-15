package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttLeaveQuotaAdjustDTO;
import com.hr.module.attendance.dto.AttLeaveQuotaQuery;
import com.hr.module.attendance.dto.AttLeaveQuotaVO;
import com.hr.module.attendance.entity.AttLeaveQuota;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper.EmployeeInfo;
import com.hr.module.attendance.mapper.AttLeaveQuotaMapper;
import com.hr.module.attendance.service.AttLeaveQuotaService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttLeaveQuotaServiceImpl implements AttLeaveQuotaService {

    private final AttLeaveQuotaMapper attLeaveQuotaMapper;
    private final AttendanceEmployeeHelper employeeHelper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public PageResult<AttLeaveQuotaVO> page(AttLeaveQuotaQuery query) {
        LambdaQueryWrapper<AttLeaveQuota> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttLeaveQuota::getEmployeeId, query.getEmployeeId());
        }
        // 通过关键词搜索员工
        if (StringUtils.hasText(query.getKeyword())) {
            List<HrEmployee> matched = hrEmployeeMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HrEmployee>()
                            .and(w -> w.like(HrEmployee::getName, query.getKeyword())
                                    .or().like(HrEmployee::getEmpNo, query.getKeyword()))
                            .select(HrEmployee::getId));
            List<Long> empIds = matched.stream().map(HrEmployee::getId).collect(Collectors.toList());
            if (empIds.isEmpty()) {
                wrapper.eq(AttLeaveQuota::getId, -1L);
            } else {
                wrapper.in(AttLeaveQuota::getEmployeeId, empIds);
            }
        }
        if (query.getYear() != null) {
            wrapper.eq(AttLeaveQuota::getYear, query.getYear());
        }
        wrapper.orderByDesc(AttLeaveQuota::getCreateTime);

        Page<AttLeaveQuota> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttLeaveQuota> result = attLeaveQuotaMapper.selectPage(page, wrapper);

        List<AttLeaveQuotaVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        // 填充员工信息
        Set<Long> empIds = result.getRecords().stream().map(AttLeaveQuota::getEmployeeId).collect(Collectors.toSet());
        Map<Long, EmployeeInfo> empMap = employeeHelper.getEmployeeInfoMap(empIds);
        voList.forEach(vo -> {
            EmployeeInfo info = empMap.get(vo.getEmployeeId());
            if (info != null) {
                vo.setEmpNo(info.getEmpNo());
                vo.setEmployeeName(info.getEmployeeName());
            }
        });
        PageResult<AttLeaveQuotaVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    @Transactional
    public void adjust(Long id, AttLeaveQuotaAdjustDTO dto) {
        AttLeaveQuota entity = attLeaveQuotaMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "假期额度不存在");
        }
        entity.setTotalDays(dto.getTotalDays());
        entity.setRemainDays(dto.getTotalDays().subtract(entity.getUsedDays()));
        attLeaveQuotaMapper.updateById(entity);
    }

    private AttLeaveQuotaVO toVO(AttLeaveQuota entity) {
        AttLeaveQuotaVO vo = new AttLeaveQuotaVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setLeaveType(entity.getLeaveType());
        vo.setYear(entity.getYear());
        vo.setTotalDays(entity.getTotalDays());
        vo.setUsedDays(entity.getUsedDays());
        vo.setRemainDays(entity.getRemainDays());
        return vo;
    }
}
