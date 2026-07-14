package com.hr.module.attendance.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.AttDeductionEditDTO;
import com.hr.module.attendance.dto.AttDeductionVO;
import com.hr.module.attendance.entity.AttDeduction;
import com.hr.module.attendance.mapper.AttDeductionMapper;
import com.hr.module.attendance.service.AttDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttDeductionServiceImpl implements AttDeductionService {

    private static final Map<String, String> TYPE_NAME_MAP = new HashMap<>();

    static {
        TYPE_NAME_MAP.put("miss_card", "漏打卡");
        TYPE_NAME_MAP.put("late", "迟到/早退");
        TYPE_NAME_MAP.put("early", "早退");
        TYPE_NAME_MAP.put("absent", "缺勤");
    }

    private final AttDeductionMapper attDeductionMapper;

    @Override
    public List<AttDeductionVO> list() {
        List<AttDeduction> entities = attDeductionMapper.selectList(null);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void update(Long id, AttDeductionEditDTO dto) {
        AttDeduction entity = attDeductionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "扣款配置不存在");
        }
        entity.setDeduction(dto.getDeduction());
        entity.setUnit(dto.getUnit());
        entity.setRemark(dto.getRemark());
        attDeductionMapper.updateById(entity);
    }

    private AttDeductionVO toVO(AttDeduction entity) {
        AttDeductionVO vo = new AttDeductionVO();
        vo.setId(entity.getId());
        vo.setType(entity.getType());
        vo.setTypeName(TYPE_NAME_MAP.getOrDefault(entity.getType(), entity.getType()));
        vo.setDeduction(entity.getDeduction());
        vo.setUnit(entity.getUnit());
        vo.setRemark(entity.getRemark());
        return vo;
    }
}
