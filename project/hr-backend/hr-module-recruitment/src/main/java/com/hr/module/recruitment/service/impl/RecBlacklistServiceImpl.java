package com.hr.module.recruitment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.recruitment.dto.BlacklistDTO;
import com.hr.module.recruitment.dto.BlacklistVO;
import com.hr.module.recruitment.entity.RecBlacklist;
import com.hr.module.recruitment.mapper.RecBlacklistMapper;
import com.hr.module.recruitment.service.RecBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecBlacklistServiceImpl implements RecBlacklistService {

    private final RecBlacklistMapper recBlacklistMapper;

    @Override
    public IPage<BlacklistVO> page(Integer pageNum, Integer pageSize) {
        Page<RecBlacklist> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        Page<RecBlacklist> result = recBlacklistMapper.selectPage(page,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<RecBlacklist>()
                        .orderByDesc(RecBlacklist::getCreateTime));

        List<BlacklistVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<BlacklistVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void create(BlacklistDTO dto) {
        RecBlacklist entity = new RecBlacklist();
        entity.setResumeId(dto.getResumeId());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setIdCard(dto.getIdCard());
        entity.setReason(dto.getReason());
        recBlacklistMapper.insert(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RecBlacklist entity = recBlacklistMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "黑名单记录不存在");
        }
        recBlacklistMapper.deleteById(id);
    }

    private BlacklistVO toVO(RecBlacklist entity) {
        BlacklistVO vo = new BlacklistVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPhone(entity.getPhone());
        vo.setIdCard(entity.getIdCard());
        vo.setReason(entity.getReason());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
