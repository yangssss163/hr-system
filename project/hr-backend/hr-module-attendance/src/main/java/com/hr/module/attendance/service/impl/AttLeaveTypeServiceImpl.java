package com.hr.module.attendance.service.impl;

import com.hr.module.attendance.dto.*;
import com.hr.module.attendance.entity.AttLeaveType;
import com.hr.module.attendance.mapper.AttLeaveTypeMapper;
import com.hr.module.attendance.service.AttLeaveTypeService;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttLeaveTypeServiceImpl implements AttLeaveTypeService {

    private final AttLeaveTypeMapper attLeaveTypeMapper;

    @Override
    public List<AttLeaveTypeVO> list() {
        List<AttLeaveType> entities = attLeaveTypeMapper.selectList(null);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public AttLeaveTypeVO getById(Long id) {
        AttLeaveType entity = attLeaveTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "假期类型不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(AttLeaveTypeDTO dto) {
        AttLeaveType entity = new AttLeaveType();
        applyDTO(entity, dto);
        attLeaveTypeMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, AttLeaveTypeDTO dto) {
        AttLeaveType entity = attLeaveTypeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "假期类型不存在");
        }
        applyDTO(entity, dto);
        attLeaveTypeMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        attLeaveTypeMapper.deleteById(id);
    }

    private void applyDTO(AttLeaveType entity, AttLeaveTypeDTO dto) {
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setDefaultDays(dto.getDefaultDays());
        entity.setEnabled(dto.getEnabled());
    }

    private AttLeaveTypeVO toVO(AttLeaveType entity) {
        AttLeaveTypeVO vo = new AttLeaveTypeVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setCode(entity.getCode());
        vo.setDefaultDays(entity.getDefaultDays());
        vo.setEnabled(entity.getEnabled());
        return vo;
    }
}
