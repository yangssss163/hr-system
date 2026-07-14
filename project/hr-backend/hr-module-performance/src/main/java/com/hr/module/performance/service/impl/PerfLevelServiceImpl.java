package com.hr.module.performance.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.performance.dto.PerfLevelDTO;
import com.hr.module.performance.entity.PerfLevel;
import com.hr.module.performance.mapper.PerfLevelMapper;
import com.hr.module.performance.service.PerfLevelService;
import com.hr.module.performance.vo.PerfLevelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfLevelServiceImpl implements PerfLevelService {

    private final PerfLevelMapper perfLevelMapper;

    @Override
    public List<PerfLevelVO> list() {
        List<PerfLevel> entities = perfLevelMapper.selectList(null);
        return entities.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public PerfLevelVO getById(Long id) {
        PerfLevel entity = perfLevelMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "等级不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(PerfLevelDTO dto) {
        PerfLevel entity = new PerfLevel();
        entity.setName(dto.getName());
        entity.setScoreMin(dto.getScoreMin());
        entity.setScoreMax(dto.getScoreMax());
        entity.setCoefficient(dto.getCoefficient());
        entity.setSort(dto.getSort());
        perfLevelMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, PerfLevelDTO dto) {
        PerfLevel entity = perfLevelMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "等级不存在");
        }
        entity.setName(dto.getName());
        entity.setScoreMin(dto.getScoreMin());
        entity.setScoreMax(dto.getScoreMax());
        entity.setCoefficient(dto.getCoefficient());
        entity.setSort(dto.getSort());
        perfLevelMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PerfLevel entity = perfLevelMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "等级不存在");
        }
        perfLevelMapper.deleteById(id);
    }

    private PerfLevelVO toVO(PerfLevel entity) {
        PerfLevelVO vo = new PerfLevelVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setScoreMin(entity.getScoreMin());
        vo.setScoreMax(entity.getScoreMax());
        vo.setCoefficient(entity.getCoefficient());
        vo.setSort(entity.getSort());
        return vo;
    }
}
