package com.hr.module.attendance.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.*;
import com.hr.module.attendance.entity.AttHoliday;
import com.hr.module.attendance.mapper.AttHolidayMapper;
import com.hr.module.attendance.service.AttHolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttHolidayServiceImpl implements AttHolidayService {

    private final AttHolidayMapper attHolidayMapper;

    @Override
    public AttHolidayVO list(Integer year) {
        List<AttHoliday> entities;
        if (year != null) {
            entities = attHolidayMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AttHoliday>()
                            .eq(AttHoliday::getYear, year));
        } else {
            entities = attHolidayMapper.selectList(null);
        }
        AttHolidayVO vo = new AttHolidayVO();
        vo.setYear(year);
        vo.setItems(entities.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public void create(AttHolidayDTO dto) {
        AttHoliday entity = new AttHoliday();
        applyDTO(entity, dto);
        attHolidayMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, AttHolidayDTO dto) {
        AttHoliday entity = attHolidayMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "假期计划不存在");
        }
        applyDTO(entity, dto);
        attHolidayMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        attHolidayMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void copy(Long id, Integer targetYear) {
        AttHoliday source = attHolidayMapper.selectById(id);
        if (source == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "假期计划不存在");
        }
        AttHoliday target = new AttHoliday();
        target.setYear(targetYear);
        target.setName(source.getName());
        target.setDate(LocalDate.of(targetYear, source.getDate().getMonth(), source.getDate().getDayOfMonth()));
        target.setDays(source.getDays());
        attHolidayMapper.insert(target);
    }

    private void applyDTO(AttHoliday entity, AttHolidayDTO dto) {
        entity.setYear(dto.getYear());
        entity.setName(dto.getName());
        entity.setDate(LocalDate.parse(dto.getDate()));
        entity.setDays(dto.getDays());
    }

    private AttHolidayItemVO toItemVO(AttHoliday entity) {
        AttHolidayItemVO vo = new AttHolidayItemVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDate(entity.getDate());
        vo.setDays(entity.getDays());
        return vo;
    }
}
