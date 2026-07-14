package com.hr.module.performance.service.impl;

import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.performance.dto.PerfSalaryDTO;
import com.hr.module.performance.entity.PerfLevel;
import com.hr.module.performance.entity.PerfSalary;
import com.hr.module.performance.mapper.PerfLevelMapper;
import com.hr.module.performance.mapper.PerfSalaryMapper;
import com.hr.module.performance.service.PerfSalaryService;
import com.hr.module.performance.vo.PerfSalaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfSalaryServiceImpl implements PerfSalaryService {

    private final PerfSalaryMapper perfSalaryMapper;
    private final PerfLevelMapper perfLevelMapper;

    @Override
    public List<PerfSalaryVO> list() {
        List<PerfSalary> entities = perfSalaryMapper.selectList(null);
        Map<Long, String> levelNameMap = perfLevelMapper.selectList(null).stream()
                .collect(Collectors.toMap(PerfLevel::getId, PerfLevel::getName, (a, b) -> a));
        return entities.stream()
                .map(e -> toVO(e, levelNameMap))
                .collect(Collectors.toList());
    }

    @Override
    public PerfSalaryVO getById(Long id) {
        PerfSalary entity = perfSalaryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "薪酬标准不存在");
        }
        String levelName = null;
        if (entity.getLevelId() != null) {
            PerfLevel level = perfLevelMapper.selectById(entity.getLevelId());
            levelName = level != null ? level.getName() : null;
        }
        return toVO(entity, levelName);
    }

    @Override
    @Transactional
    public void create(PerfSalaryDTO dto) {
        PerfSalary entity = new PerfSalary();
        entity.setLevelId(dto.getLevelId());
        entity.setSalaryRange(dto.getSalaryRange());
        entity.setSort(dto.getSort());
        perfSalaryMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, PerfSalaryDTO dto) {
        PerfSalary entity = perfSalaryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "薪酬标准不存在");
        }
        entity.setLevelId(dto.getLevelId());
        entity.setSalaryRange(dto.getSalaryRange());
        entity.setSort(dto.getSort());
        perfSalaryMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PerfSalary entity = perfSalaryMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "薪酬标准不存在");
        }
        perfSalaryMapper.deleteById(id);
    }

    private PerfSalaryVO toVO(PerfSalary entity, Map<Long, String> levelNameMap) {
        PerfSalaryVO vo = new PerfSalaryVO();
        vo.setId(entity.getId());
        vo.setLevelId(entity.getLevelId());
        vo.setLevelName(levelNameMap.getOrDefault(entity.getLevelId(), null));
        vo.setSalaryRange(entity.getSalaryRange());
        vo.setSort(entity.getSort());
        return vo;
    }

    private PerfSalaryVO toVO(PerfSalary entity, String levelName) {
        PerfSalaryVO vo = new PerfSalaryVO();
        vo.setId(entity.getId());
        vo.setLevelId(entity.getLevelId());
        vo.setLevelName(levelName);
        vo.setSalaryRange(entity.getSalaryRange());
        vo.setSort(entity.getSort());
        return vo;
    }
}
