package com.hr.module.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.exception.BusinessException;
import com.hr.module.office.dto.SysDocumentDTO;
import com.hr.module.office.dto.SysDocumentQuery;
import com.hr.module.office.dto.SysDocumentVO;
import com.hr.module.office.entity.SysDocument;
import com.hr.module.office.mapper.SysDocumentMapper;
import com.hr.module.office.service.SysDocumentService;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SysDocumentServiceImpl implements SysDocumentService {

    private final SysDocumentMapper sysDocumentMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<SysDocumentVO> page(SysDocumentQuery query) {
        LambdaQueryWrapper<SysDocument> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(SysDocument::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(SysDocument::getCategory, query.getCategory());
        }
        if (query.getIsPublic() != null) {
            wrapper.eq(SysDocument::getIsPublic, query.getIsPublic());
        }
        wrapper.orderByDesc(SysDocument::getCreateTime);
        Page<SysDocument> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SysDocument> result = sysDocumentMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SysDocumentVO getById(Long id) {
        SysDocument entity = sysDocumentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("文档不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(SysDocumentDTO dto) {
        SysDocument entity = new SysDocument();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());
        entity.setParentId(dto.getParentId());
        entity.setCreatorId(dto.getCreatorId());
        entity.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : 0);
        entity.setFileUrl(dto.getFileUrl());
        sysDocumentMapper.insert(entity);
    }

    @Override
    public void update(Long id, SysDocumentDTO dto) {
        SysDocument entity = sysDocumentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("文档不存在");
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setCategory(dto.getCategory());
        entity.setParentId(dto.getParentId());
        entity.setCreatorId(dto.getCreatorId());
        entity.setIsPublic(dto.getIsPublic());
        entity.setFileUrl(dto.getFileUrl());
        sysDocumentMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        sysDocumentMapper.deleteById(id);
    }

    private SysDocumentVO toVO(SysDocument entity) {
        SysDocumentVO vo = new SysDocumentVO();
        vo.setId(entity.getId());
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setCategory(entity.getCategory());
        vo.setParentId(entity.getParentId());
        vo.setCreatorId(entity.getCreatorId());
        if (entity.getCreatorId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getCreatorId());
            if (user != null) vo.setCreatorName(user.getRealName());
        }
        vo.setIsPublic(entity.getIsPublic());
        vo.setFileUrl(entity.getFileUrl());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
