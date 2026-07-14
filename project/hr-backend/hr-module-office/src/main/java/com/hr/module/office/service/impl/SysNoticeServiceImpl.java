package com.hr.module.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.exception.BusinessException;
import com.hr.module.office.dto.SysNoticeDTO;
import com.hr.module.office.dto.SysNoticeQuery;
import com.hr.module.office.dto.SysNoticeVO;
import com.hr.module.office.entity.SysNotice;
import com.hr.module.office.mapper.SysNoticeMapper;
import com.hr.module.office.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl implements SysNoticeService {

    private final SysNoticeMapper sysNoticeMapper;

    @Override
    public IPage<SysNoticeVO> page(SysNoticeQuery query) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysNotice::getTitle, query.getKeyword());
        }
        if (query.getType() != null) {
            wrapper.eq(SysNotice::getType, query.getType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysNotice::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        Page<SysNotice> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysNotice> result = sysNoticeMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SysNoticeVO getById(Long id) {
        SysNotice entity = sysNoticeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("公告不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(SysNoticeDTO dto) {
        SysNotice entity = new SysNotice();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        entity.setPublisherId(dto.getPublisherId());
        entity.setPublishTime(dto.getPublishTime());
        sysNoticeMapper.insert(entity);
    }

    @Override
    public void update(Long id, SysNoticeDTO dto) {
        SysNotice entity = sysNoticeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("公告不存在");
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setType(dto.getType());
        entity.setStatus(dto.getStatus());
        entity.setPublisherId(dto.getPublisherId());
        entity.setPublishTime(dto.getPublishTime());
        sysNoticeMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        sysNoticeMapper.deleteById(id);
    }

    private SysNoticeVO toVO(SysNotice entity) {
        SysNoticeVO vo = new SysNoticeVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setType(entity.getType());
        vo.setStatus(entity.getStatus());
        vo.setPublisherId(entity.getPublisherId());
        vo.setPublishTime(entity.getPublishTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
