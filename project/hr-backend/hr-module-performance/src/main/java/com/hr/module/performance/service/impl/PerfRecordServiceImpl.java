package com.hr.module.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.common.result.PageResult;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.performance.dto.PerfRecordDTO;
import com.hr.module.performance.dto.PerfRecordItemDTO;
import com.hr.module.performance.entity.PerfLevel;
import com.hr.module.performance.entity.PerfPlan;
import com.hr.module.performance.entity.PerfRecord;
import com.hr.module.performance.entity.PerfRecordItem;
import com.hr.module.performance.mapper.PerfLevelMapper;
import com.hr.module.performance.mapper.PerfPlanMapper;
import com.hr.module.performance.mapper.PerfRecordItemMapper;
import com.hr.module.performance.mapper.PerfRecordMapper;
import com.hr.module.performance.query.PerfRecordQuery;
import com.hr.module.performance.service.PerfRecordService;
import com.hr.module.performance.vo.PerfRecordDetailVO;
import com.hr.module.performance.vo.PerfRecordItemVO;
import com.hr.module.performance.vo.PerfRecordVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfRecordServiceImpl implements PerfRecordService {

    private final PerfRecordMapper perfRecordMapper;
    private final PerfRecordItemMapper perfRecordItemMapper;
    private final PerfPlanMapper perfPlanMapper;
    private final PerfLevelMapper perfLevelMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<PerfRecordVO> page(PerfRecordQuery query) {
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        if (query.getPlanId() != null) {
            wrapper.eq(PerfRecord::getPlanId, query.getPlanId());
        }
        if (query.getEmployeeId() != null) {
            wrapper.eq(PerfRecord::getEmployeeId, query.getEmployeeId());
        }
        if (query.getLevelId() != null) {
            wrapper.eq(PerfRecord::getLevelId, query.getLevelId());
        }
        wrapper.orderByDesc(PerfRecord::getCreateTime);

        Page<PerfRecord> page = new Page<>(query.getPage(), query.getPageSize());
        Page<PerfRecord> result = perfRecordMapper.selectPage(page, wrapper);

        // 如果指定了 deptId，需要按员工所在部门过滤
        List<PerfRecord> records = result.getRecords();
        if (query.getDeptId() != null && !records.isEmpty()) {
            List<Long> empIds = records.stream()
                    .map(PerfRecord::getEmployeeId).distinct().collect(Collectors.toList());
            List<Long> deptEmpIds = hrEmployeeMapper.selectBatchIds(empIds).stream()
                    .filter(e -> query.getDeptId().equals(e.getDeptId()))
                    .map(HrEmployee::getId).collect(Collectors.toList());
            records = records.stream()
                    .filter(r -> deptEmpIds.contains(r.getEmployeeId()))
                    .collect(Collectors.toList());
        }

        List<PerfRecordVO> voList = records.stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageResult<PerfRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal((long) records.size());
        pageResult.setPage((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public PerfRecordDetailVO getById(Long id) {
        PerfRecord entity = perfRecordMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核记录不存在");
        }
        PerfRecordDetailVO vo = new PerfRecordDetailVO();
        vo.setId(entity.getId());
        vo.setPlanId(entity.getPlanId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setTotalScore(entity.getTotalScore());
        vo.setLevelId(entity.getLevelId());
        vo.setEvaluation(entity.getEvaluation());
        vo.setEvaluateTime(entity.getEvaluateTime());

        // 计划名称
        if (entity.getPlanId() != null) {
            PerfPlan plan = perfPlanMapper.selectById(entity.getPlanId());
            vo.setPlanName(plan != null ? plan.getName() : null);
        }
        // 员工信息
        if (entity.getEmployeeId() != null) {
            HrEmployee emp = hrEmployeeMapper.selectById(entity.getEmployeeId());
            if (emp != null) {
                vo.setEmpNo(emp.getEmpNo());
                vo.setEmployeeName(emp.getName());
                if (emp.getDeptId() != null) {
                    SysDept dept = sysDeptMapper.selectById(emp.getDeptId());
                    vo.setDeptName(dept != null ? dept.getName() : null);
                }
            }
        }
        // 等级名称
        if (entity.getLevelId() != null) {
            PerfLevel level = perfLevelMapper.selectById(entity.getLevelId());
            vo.setLevelName(level != null ? level.getName() : null);
        }
        // 评估人
        if (entity.getEvaluatorId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getEvaluatorId());
            vo.setEvaluatorName(user != null ? user.getRealName() : null);
        }
        // 指标明细
        List<PerfRecordItem> items = perfRecordItemMapper.selectList(
                new LambdaQueryWrapper<PerfRecordItem>().eq(PerfRecordItem::getRecordId, id));
        List<PerfRecordItemVO> itemVOs = items.stream().map(item -> {
            PerfRecordItemVO iv = new PerfRecordItemVO();
            iv.setId(item.getId());
            iv.setIndicator(item.getIndicator());
            iv.setWeight(item.getWeight());
            iv.setScore(item.getScore());
            return iv;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }

    @Override
    @Transactional
    public void create(PerfRecordDTO dto) {
        // 校验同一员工在同一考核计划下是否已存在记录
        Long count = perfRecordMapper.selectCount(new LambdaQueryWrapper<PerfRecord>()
                .eq(PerfRecord::getEmployeeId, dto.getEmployeeId())
                .eq(PerfRecord::getPlanId, dto.getPlanId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该员工在此考核计划下已有考核记录");
        }

        PerfRecord entity = new PerfRecord();
        entity.setPlanId(dto.getPlanId());
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setTotalScore(dto.getTotalScore());
        entity.setLevelId(dto.getLevelId());
        entity.setEvaluation(dto.getEvaluation());
        entity.setEvaluateTime(LocalDateTime.now());
        entity.setStatus(1);

        // 设置评估人：优先使用前端传入的，否则使用当前登录用户
        if (dto.getEvaluatorId() != null) {
            entity.setEvaluatorId(dto.getEvaluatorId());
        } else {
            entity.setEvaluatorId(SecurityUtils.getCurrentUserId());
        }

        perfRecordMapper.insert(entity);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (PerfRecordItemDTO itemDTO : dto.getItems()) {
                PerfRecordItem item = new PerfRecordItem();
                item.setRecordId(entity.getId());
                item.setIndicator(itemDTO.getIndicator());
                item.setWeight(itemDTO.getWeight());
                item.setScore(itemDTO.getScore());
                perfRecordItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void update(Long id, PerfRecordDTO dto) {
        PerfRecord entity = perfRecordMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核记录不存在");
        }

        // 校验唯一性（排除自身）
        Long count = perfRecordMapper.selectCount(new LambdaQueryWrapper<PerfRecord>()
                .eq(PerfRecord::getEmployeeId, dto.getEmployeeId())
                .eq(PerfRecord::getPlanId, dto.getPlanId())
                .ne(PerfRecord::getId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "该员工在此考核计划下已有考核记录");
        }

        entity.setPlanId(dto.getPlanId());
        entity.setEmployeeId(dto.getEmployeeId());
        entity.setTotalScore(dto.getTotalScore());
        entity.setLevelId(dto.getLevelId());
        entity.setEvaluation(dto.getEvaluation());
        entity.setEvaluateTime(LocalDateTime.now());

        // 设置评估人
        if (dto.getEvaluatorId() != null) {
            entity.setEvaluatorId(dto.getEvaluatorId());
        } else {
            entity.setEvaluatorId(SecurityUtils.getCurrentUserId());
        }

        perfRecordMapper.updateById(entity);

        // 重新保存指标明细
        perfRecordItemMapper.delete(new LambdaQueryWrapper<PerfRecordItem>()
                .eq(PerfRecordItem::getRecordId, id));
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (PerfRecordItemDTO itemDTO : dto.getItems()) {
                PerfRecordItem item = new PerfRecordItem();
                item.setRecordId(id);
                item.setIndicator(itemDTO.getIndicator());
                item.setWeight(itemDTO.getWeight());
                item.setScore(itemDTO.getScore());
                perfRecordItemMapper.insert(item);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PerfRecord entity = perfRecordMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "考核记录不存在");
        }
        // 删除关联的指标明细
        perfRecordItemMapper.delete(new LambdaQueryWrapper<PerfRecordItem>()
                .eq(PerfRecordItem::getRecordId, id));
        // 删除考核记录
        perfRecordMapper.deleteById(id);
    }

    private PerfRecordVO toVO(PerfRecord entity) {
        PerfRecordVO vo = new PerfRecordVO();
        vo.setId(entity.getId());
        vo.setPlanId(entity.getPlanId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setTotalScore(entity.getTotalScore());
        vo.setLevelId(entity.getLevelId());
        vo.setEvaluateTime(entity.getEvaluateTime());

        if (entity.getPlanId() != null) {
            PerfPlan plan = perfPlanMapper.selectById(entity.getPlanId());
            vo.setPlanName(plan != null ? plan.getName() : null);
        }
        if (entity.getEmployeeId() != null) {
            HrEmployee emp = hrEmployeeMapper.selectById(entity.getEmployeeId());
            if (emp != null) {
                vo.setEmpNo(emp.getEmpNo());
                vo.setEmployeeName(emp.getName());
                if (emp.getDeptId() != null) {
                    SysDept dept = sysDeptMapper.selectById(emp.getDeptId());
                    vo.setDeptName(dept != null ? dept.getName() : null);
                }
            }
        }
        if (entity.getLevelId() != null) {
            PerfLevel level = perfLevelMapper.selectById(entity.getLevelId());
            vo.setLevelName(level != null ? level.getName() : null);
        }
        if (entity.getEvaluatorId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getEvaluatorId());
            vo.setEvaluatorName(user != null ? user.getRealName() : null);
        }
        return vo;
    }
}
