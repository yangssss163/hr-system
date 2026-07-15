package com.hr.module.attendance.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.AttCardRuleDTO;
import com.hr.module.attendance.dto.AttCardRuleVO;
import com.hr.module.attendance.entity.AttCardRule;
import com.hr.module.attendance.mapper.AttCardRuleMapper;
import com.hr.module.attendance.service.AttCardRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttCardRuleServiceImpl implements AttCardRuleService {

    private final AttCardRuleMapper attCardRuleMapper;

    @Override
    public List<AttCardRuleVO> list() {
        List<AttCardRule> entities = attCardRuleMapper.selectList(null);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public AttCardRuleVO getById(Long id) {
        AttCardRule entity = attCardRuleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "取卡规则不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(AttCardRuleDTO dto) {
        AttCardRule entity = new AttCardRule();
        applyDTO(entity, dto);
        attCardRuleMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, AttCardRuleDTO dto) {
        AttCardRule entity = attCardRuleMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "取卡规则不存在");
        }
        applyDTO(entity, dto);
        attCardRuleMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        attCardRuleMapper.deleteById(id);
    }

    private void applyDTO(AttCardRule entity, AttCardRuleDTO dto) {
        entity.setName(dto.getName());
        entity.setMinCardCount(dto.getMinCardCount());
        entity.setAllowOvertime(dto.getAllowOvertime() != null && dto.getAllowOvertime() ? 1 : 0);
        entity.setStatus(dto.getStatus());
    }

    private AttCardRuleVO toVO(AttCardRule entity) {
        AttCardRuleVO vo = new AttCardRuleVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setMinCardCount(entity.getMinCardCount());
        vo.setAllowOvertime(entity.getAllowOvertime());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
