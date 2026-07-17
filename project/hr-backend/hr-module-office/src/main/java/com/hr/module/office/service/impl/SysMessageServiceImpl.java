package com.hr.module.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.exception.BusinessException;
import com.hr.module.office.dto.SysMessageDTO;
import com.hr.module.office.dto.SysMessageQuery;
import com.hr.module.office.dto.SysMessageVO;
import com.hr.module.office.entity.SysMessage;
import com.hr.module.office.mapper.SysMessageMapper;
import com.hr.module.office.service.SysMessageService;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysMessageServiceImpl implements SysMessageService {

    private final SysMessageMapper sysMessageMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<SysMessageVO> page(SysMessageQuery query) {
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysMessage::getTitle, query.getKeyword());
        }
        if (query.getIsRead() != null) {
            wrapper.eq(SysMessage::getIsRead, query.getIsRead());
        }
        if (query.getUserId() != null) {
            wrapper.and(w -> w.eq(SysMessage::getSenderId, query.getUserId()).or().eq(SysMessage::getReceiverId, query.getUserId()));
        }
        if (query.getReceiverId() != null) {
            wrapper.eq(SysMessage::getReceiverId, query.getReceiverId());
        }
        if (query.getSenderId() != null) {
            wrapper.eq(SysMessage::getSenderId, query.getSenderId());
        }
        wrapper.orderByDesc(SysMessage::getCreateTime);
        Page<SysMessage> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysMessage> result = sysMessageMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SysMessageVO getById(Long id) {
        SysMessage entity = sysMessageMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("消息不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(SysMessageDTO dto) {
        SysMessage entity = new SysMessage();
        entity.setSenderId(dto.getSenderId());
        entity.setReceiverId(dto.getReceiverId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setIsRead(dto.getIsRead() != null ? dto.getIsRead() : 0);
        entity.setSendTime(dto.getSendTime());
        sysMessageMapper.insert(entity);
    }

    @Override
    public void update(Long id, SysMessageDTO dto) {
        SysMessage entity = sysMessageMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("消息不存在");
        }
        entity.setSenderId(dto.getSenderId());
        entity.setReceiverId(dto.getReceiverId());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setIsRead(dto.getIsRead());
        entity.setSendTime(dto.getSendTime());
        sysMessageMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        sysMessageMapper.deleteById(id);
    }

    private SysMessageVO toVO(SysMessage entity) {
        SysMessageVO vo = new SysMessageVO();
        vo.setId(entity.getId());
        vo.setSenderId(entity.getSenderId());
        if (entity.getSenderId() != null) {
            SysUser sender = sysUserMapper.selectById(entity.getSenderId());
            if (sender != null) vo.setSenderName(sender.getRealName());
        }
        vo.setReceiverId(entity.getReceiverId());
        if (entity.getReceiverId() != null) {
            SysUser receiver = sysUserMapper.selectById(entity.getReceiverId());
            if (receiver != null) vo.setReceiverName(receiver.getRealName());
        }
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setIsRead(entity.getIsRead());
        vo.setSendTime(entity.getSendTime());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
