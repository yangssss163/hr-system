package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.attendance.dto.AttShiftDTO;
import com.hr.module.attendance.dto.AttShiftVO;
import com.hr.module.attendance.entity.AttShift;
import com.hr.module.attendance.mapper.AttShiftMapper;
import com.hr.module.attendance.service.AttShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttShiftServiceImpl implements AttShiftService {

    private final AttShiftMapper attShiftMapper;

    @Override
    public List<AttShiftVO> list() {
        List<AttShift> entities = attShiftMapper.selectList(null);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public AttShiftVO getById(Long id) {
        AttShift entity = attShiftMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "班次不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(AttShiftDTO dto) {
        AttShift entity = new AttShift();
        applyDTO(entity, dto);
        attShiftMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, AttShiftDTO dto) {
        AttShift entity = attShiftMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "班次不存在");
        }
        applyDTO(entity, dto);
        attShiftMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        attShiftMapper.deleteById(id);
    }

    private void applyDTO(AttShift entity, AttShiftDTO dto) {
        entity.setName(dto.getName());
        entity.setStartTime(LocalTime.parse(dto.getStartTime()));
        entity.setEndTime(LocalTime.parse(dto.getEndTime()));
        entity.setLateBuffer(dto.getLateBuffer());
        entity.setEarlyBuffer(dto.getEarlyBuffer());
        entity.setStatus(dto.getStatus());
    }

    private AttShiftVO toVO(AttShift entity) {
        AttShiftVO vo = new AttShiftVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setStartTime(entity.getStartTime() != null ? entity.getStartTime().toString() : null);
        vo.setEndTime(entity.getEndTime() != null ? entity.getEndTime().toString() : null);
        vo.setLateBuffer(entity.getLateBuffer());
        vo.setEarlyBuffer(entity.getEarlyBuffer());
        vo.setStatus(entity.getStatus());
        return vo;
    }
}
