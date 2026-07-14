package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.*;
import com.hr.module.attendance.entity.AttRecord;
import com.hr.module.attendance.mapper.AttRecordMapper;
import com.hr.module.attendance.service.AttRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttRecordServiceImpl implements AttRecordService {

    private final AttRecordMapper attRecordMapper;

    @Override
    public IPage<AttRecordVO> page(AttRecordQuery query) {
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttRecord::getEmployeeId, query.getEmployeeId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(AttRecord::getStatus, query.getStatus());
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

        List<AttRecordVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<AttRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void batchFix(AttRecordBatchFixDTO dto) {
        List<AttRecord> records = attRecordMapper.selectBatchIds(dto.getIds());
        for (AttRecord record : records) {
            if (StringUtils.hasText(dto.getCheckIn())) {
                record.setCheckIn(LocalDateTime.parse(dto.getCheckIn()));
            }
            if (StringUtils.hasText(dto.getCheckOut())) {
                record.setCheckOut(LocalDateTime.parse(dto.getCheckOut()));
            }
            attRecordMapper.updateById(record);
        }
    }

    private AttRecordVO toVO(AttRecord entity) {
        AttRecordVO vo = new AttRecordVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setRecordDate(entity.getRecordDate());
        vo.setCheckIn(entity.getCheckIn());
        vo.setCheckOut(entity.getCheckOut());
        vo.setStatus(entity.getStatus());
        vo.setSource(entity.getSource());
        return vo;
    }
}
