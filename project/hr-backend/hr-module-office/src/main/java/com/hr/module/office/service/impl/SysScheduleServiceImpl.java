package com.hr.module.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.exception.BusinessException;
import com.hr.module.office.dto.SysScheduleDTO;
import com.hr.module.office.dto.SysScheduleQuery;
import com.hr.module.office.dto.SysScheduleVO;
import com.hr.module.office.entity.SysSchedule;
import com.hr.module.office.mapper.SysScheduleMapper;
import com.hr.module.office.service.SysScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysScheduleServiceImpl implements SysScheduleService {

    private final SysScheduleMapper sysScheduleMapper;

    @Override
    public IPage<SysScheduleVO> page(SysScheduleQuery query) {
        LambdaQueryWrapper<SysSchedule> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysSchedule::getTitle, query.getKeyword());
        }
        if (query.getUserId() != null) {
            wrapper.eq(SysSchedule::getUserId, query.getUserId());
        }
        if (query.getStartTime() != null) {
            wrapper.ge(SysSchedule::getStartTime, query.getStartTime());
        }
        if (query.getEndTime() != null) {
            wrapper.le(SysSchedule::getEndTime, query.getEndTime());
        }
        wrapper.orderByDesc(SysSchedule::getCreateTime);
        Page<SysSchedule> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysSchedule> result = sysScheduleMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SysScheduleVO getById(Long id) {
        SysSchedule entity = sysScheduleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("日程不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(SysScheduleDTO dto) {
        SysSchedule entity = new SysSchedule();
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setAllDay(dto.getAllDay() != null ? dto.getAllDay() : 0);
        entity.setColor(dto.getColor());
        sysScheduleMapper.insert(entity);
    }

    @Override
    public void update(Long id, SysScheduleDTO dto) {
        SysSchedule entity = sysScheduleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("日程不存在");
        }
        entity.setUserId(dto.getUserId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setAllDay(dto.getAllDay());
        entity.setColor(dto.getColor());
        sysScheduleMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        sysScheduleMapper.deleteById(id);
    }

    private SysScheduleVO toVO(SysSchedule entity) {
        SysScheduleVO vo = new SysScheduleVO();
        vo.setId(entity.getId());
        vo.setUserId(entity.getUserId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setStartTime(entity.getStartTime());
        vo.setEndTime(entity.getEndTime());
        vo.setAllDay(entity.getAllDay());
        vo.setColor(entity.getColor());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
