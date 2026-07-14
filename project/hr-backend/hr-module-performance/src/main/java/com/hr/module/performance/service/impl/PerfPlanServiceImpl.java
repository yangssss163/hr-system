package com.hr.module.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.performance.dto.PerfPlanDTO;
import com.hr.module.performance.entity.PerfPlan;
import com.hr.module.performance.entity.PerfPlanEmployee;
import com.hr.module.performance.mapper.PerfPlanEmployeeMapper;
import com.hr.module.performance.mapper.PerfPlanMapper;
import com.hr.module.performance.query.PerfPlanQuery;
import com.hr.module.performance.service.PerfPlanService;
import com.hr.module.performance.vo.PerfPlanVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.mapper.SysDeptMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfPlanServiceImpl implements PerfPlanService {

    private final PerfPlanMapper perfPlanMapper;
    private final PerfPlanEmployeeMapper perfPlanEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;

    @Override
    public IPage<PerfPlanVO> page(PerfPlanQuery query) {
        LambdaQueryWrapper<PerfPlan> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(PerfPlan::getName, query.getKeyword());
        }
        if (query.getStatus() != null) {
            wrapper.eq(PerfPlan::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(PerfPlan::getCreateTime);

        Page<PerfPlan> page = new Page<>(query.getPage(), query.getPageSize());
        Page<PerfPlan> result = perfPlanMapper.selectPage(page, wrapper);

        List<PerfPlanVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        Page<PerfPlanVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public PerfPlanVO getById(Long id) {
        PerfPlan entity = perfPlanMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核计划不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(PerfPlanDTO dto) {
        PerfPlan entity = applyDTO(new PerfPlan(), dto);
        perfPlanMapper.insert(entity);

        Long planId = entity.getId();
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            for (Long empId : dto.getEmployeeIds()) {
                PerfPlanEmployee pe = new PerfPlanEmployee();
                pe.setPlanId(planId);
                pe.setEmployeeId(empId);
                perfPlanEmployeeMapper.insert(pe);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, PerfPlanDTO dto) {
        PerfPlan entity = perfPlanMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核计划不存在");
        }
        applyDTO(entity, dto);
        perfPlanMapper.updateById(entity);

        // 重新关联员工
        perfPlanEmployeeMapper.delete(new LambdaQueryWrapper<PerfPlanEmployee>()
                .eq(PerfPlanEmployee::getPlanId, id));
        if (dto.getEmployeeIds() != null && !dto.getEmployeeIds().isEmpty()) {
            for (Long empId : dto.getEmployeeIds()) {
                PerfPlanEmployee pe = new PerfPlanEmployee();
                pe.setPlanId(id);
                pe.setEmployeeId(empId);
                perfPlanEmployeeMapper.insert(pe);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PerfPlan entity = perfPlanMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核计划不存在");
        }
        perfPlanMapper.deleteById(id);
        perfPlanEmployeeMapper.delete(new LambdaQueryWrapper<PerfPlanEmployee>()
                .eq(PerfPlanEmployee::getPlanId, id));
    }

    private PerfPlan applyDTO(PerfPlan entity, PerfPlanDTO dto) {
        entity.setName(dto.getName());
        entity.setDeptId(dto.getDeptId());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private PerfPlanVO toVO(PerfPlan entity) {
        PerfPlanVO vo = new PerfPlanVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setDeptId(entity.getDeptId());
        if (entity.getDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(entity.getDeptId());
            vo.setDeptName(dept != null ? dept.getName() : null);
        }
        vo.setStartDate(entity.getStartDate());
        vo.setEndDate(entity.getEndDate());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
