package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.AttLeaveQuotaAdjustDTO;
import com.hr.module.attendance.dto.AttLeaveQuotaQuery;
import com.hr.module.attendance.dto.AttLeaveQuotaVO;
import com.hr.module.attendance.entity.AttLeaveQuota;
import com.hr.module.attendance.mapper.AttLeaveQuotaMapper;
import com.hr.module.attendance.service.AttLeaveQuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttLeaveQuotaServiceImpl implements AttLeaveQuotaService {

    private final AttLeaveQuotaMapper attLeaveQuotaMapper;

    @Override
    public IPage<AttLeaveQuotaVO> page(AttLeaveQuotaQuery query) {
        LambdaQueryWrapper<AttLeaveQuota> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttLeaveQuota::getEmployeeId, query.getEmployeeId());
        }
        if (query.getYear() != null) {
            wrapper.eq(AttLeaveQuota::getYear, query.getYear());
        }
        wrapper.orderByDesc(AttLeaveQuota::getCreateTime);

        Page<AttLeaveQuota> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttLeaveQuota> result = attLeaveQuotaMapper.selectPage(page, wrapper);

        List<AttLeaveQuotaVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<AttLeaveQuotaVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
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
