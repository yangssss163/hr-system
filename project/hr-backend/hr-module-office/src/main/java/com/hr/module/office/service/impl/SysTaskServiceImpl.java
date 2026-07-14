package com.hr.module.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.exception.BusinessException;
import com.hr.module.office.dto.SysTaskDTO;
import com.hr.module.office.dto.SysTaskQuery;
import com.hr.module.office.dto.SysTaskVO;
import com.hr.module.office.entity.SysTask;
import com.hr.module.office.mapper.SysTaskMapper;
import com.hr.module.office.service.SysTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysTaskServiceImpl implements SysTaskService {

    private final SysTaskMapper sysTaskMapper;

    @Override
    public IPage<SysTaskVO> page(SysTaskQuery query) {
        LambdaQueryWrapper<SysTask> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysTask::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getPriority())) {
            wrapper.eq(SysTask::getPriority, query.getPriority());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(SysTask::getStatus, query.getStatus());
        }
        if (query.getAssigneeId() != null) {
            wrapper.eq(SysTask::getAssigneeId, query.getAssigneeId());
        }
        wrapper.orderByDesc(SysTask::getCreateTime);
        Page<SysTask> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysTask> result = sysTaskMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SysTaskVO getById(Long id) {
        SysTask entity = sysTaskMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("任务不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(SysTaskDTO dto) {
        SysTask entity = new SysTask();
        entity.setCreatorId(dto.getCreatorId());
        entity.setAssigneeId(dto.getAssigneeId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPriority(dto.getPriority());
        entity.setStatus(dto.getStatus());
        entity.setStartDate(dto.getStartDate());
        entity.setDueDate(dto.getDueDate());
        entity.setCompleteTime(dto.getCompleteTime());
        sysTaskMapper.insert(entity);
    }

    @Override
    public void update(Long id, SysTaskDTO dto) {
        SysTask entity = sysTaskMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("任务不存在");
        }
        entity.setCreatorId(dto.getCreatorId());
        entity.setAssigneeId(dto.getAssigneeId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPriority(dto.getPriority());
        entity.setStatus(dto.getStatus());
        entity.setStartDate(dto.getStartDate());
        entity.setDueDate(dto.getDueDate());
        entity.setCompleteTime(dto.getCompleteTime());
        sysTaskMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        sysTaskMapper.deleteById(id);
    }

    private SysTaskVO toVO(SysTask entity) {
        SysTaskVO vo = new SysTaskVO();
        vo.setId(entity.getId());
        vo.setCreatorId(entity.getCreatorId());
        vo.setAssigneeId(entity.getAssigneeId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setPriority(entity.getPriority());
        vo.setStatus(entity.getStatus());
        vo.setStartDate(entity.getStartDate());
        vo.setDueDate(entity.getDueDate());
        vo.setCompleteTime(entity.getCompleteTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
