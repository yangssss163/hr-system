package com.hr.module.recruitment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.recruitment.dto.NotifyTemplateDTO;
import com.hr.module.recruitment.dto.NotifyTemplateVO;
import com.hr.module.recruitment.entity.RecNotifyTemplate;
import com.hr.module.recruitment.mapper.RecNotifyTemplateMapper;
import com.hr.module.recruitment.service.RecNotifyTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecNotifyTemplateServiceImpl implements RecNotifyTemplateService {

    private final RecNotifyTemplateMapper recNotifyTemplateMapper;

    private static final Map<String, String> TYPE_NAMES = new LinkedHashMap<>();

    static {
        TYPE_NAMES.put("email", "邮件");
        TYPE_NAMES.put("sms", "短信");
    }

    @Override
    public IPage<NotifyTemplateVO> page(Integer pageNum, Integer pageSize) {
        Page<RecNotifyTemplate> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        Page<RecNotifyTemplate> result = recNotifyTemplateMapper.selectPage(page,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecNotifyTemplate>()
                        .orderByDesc(RecNotifyTemplate::getCreateTime));

        List<NotifyTemplateVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<NotifyTemplateVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public NotifyTemplateVO getById(Long id) {
        RecNotifyTemplate entity = recNotifyTemplateMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "模板不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(NotifyTemplateDTO dto) {
        RecNotifyTemplate entity = new RecNotifyTemplate();
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(dto.getStatus());
        recNotifyTemplateMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, NotifyTemplateDTO dto) {
        RecNotifyTemplate entity = recNotifyTemplateMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "模板不存在");
        }
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setStatus(dto.getStatus());
        recNotifyTemplateMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RecNotifyTemplate entity = recNotifyTemplateMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "模板不存在");
        }
        recNotifyTemplateMapper.deleteById(id);
    }

    private NotifyTemplateVO toVO(RecNotifyTemplate entity) {
        NotifyTemplateVO vo = new NotifyTemplateVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setType(entity.getType());
        vo.setTypeName(TYPE_NAMES.getOrDefault(entity.getType(), entity.getType()));
        vo.setTitle(entity.getTitle());
        vo.setContent(entity.getContent());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
