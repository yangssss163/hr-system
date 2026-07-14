package com.hr.module.salary.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.salary.dto.SalFieldDTO;
import com.hr.module.salary.dto.SalFieldVO;
import com.hr.module.salary.entity.SalField;
import com.hr.module.salary.mapper.SalFieldMapper;
import com.hr.module.salary.service.SalFieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalFieldServiceImpl implements SalFieldService {

    private final SalFieldMapper salFieldMapper;

    @Override
    public List<SalFieldVO> list() {
        List<SalField> fields = salFieldMapper.selectList(null);
        return fields.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public SalFieldVO getById(Long id) {
        SalField field = salFieldMapper.selectById(id);
        if (field == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "字段不存在");
        }
        return toVO(field);
    }

    @Override
    public void create(SalFieldDTO dto) {
        SalField field = applyDTO(dto);
        salFieldMapper.insert(field);
    }

    @Override
    public void update(Long id, SalFieldDTO dto) {
        SalField field = salFieldMapper.selectById(id);
        if (field == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "字段不存在");
        }
        SalField updated = applyDTO(dto);
        updated.setId(id);
        salFieldMapper.updateById(updated);
    }

    @Override
    public void delete(Long id) {
        SalField field = salFieldMapper.selectById(id);
        if (field == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "字段不存在");
        }
        salFieldMapper.deleteById(id);
    }

    private SalField applyDTO(SalFieldDTO dto) {
        SalField field = new SalField();
        field.setName(dto.getName());
        field.setCode(dto.getCode());
        field.setType(dto.getType());
        field.setFormula(dto.getFormula());
        field.setSort(dto.getSort());
        field.setStatus(dto.getStatus());
        return field;
    }

    private SalFieldVO toVO(SalField field) {
        SalFieldVO vo = new SalFieldVO();
        vo.setId(field.getId());
        vo.setName(field.getName());
        vo.setCode(field.getCode());
        vo.setType(field.getType());
        vo.setFormula(field.getFormula());
        vo.setSort(field.getSort());
        vo.setStatus(field.getStatus());
        return vo;
    }
}
