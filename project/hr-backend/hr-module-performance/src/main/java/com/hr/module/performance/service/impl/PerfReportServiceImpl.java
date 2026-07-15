package com.hr.module.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.result.PageResult;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.performance.entity.PerfLevel;
import com.hr.module.performance.entity.PerfPlan;
import com.hr.module.performance.entity.PerfRecord;
import com.hr.module.performance.mapper.PerfLevelMapper;
import com.hr.module.performance.mapper.PerfPlanMapper;
import com.hr.module.performance.mapper.PerfRecordMapper;
import com.hr.module.performance.service.PerfReportService;
import com.hr.module.performance.vo.PerfRecordVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerfReportServiceImpl implements PerfReportService {

    private final PerfRecordMapper perfRecordMapper;
    private final PerfPlanMapper perfPlanMapper;
    private final PerfLevelMapper perfLevelMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<PerfRecordVO> detail(Long planId, Long deptId, Long levelId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(PerfRecord::getPlanId, planId);
        }
        if (levelId != null) {
            wrapper.eq(PerfRecord::getLevelId, levelId);
        }
        // deptId 过滤改为 SQL 层面：先查出该部门下所有员工ID，再用 IN 条件过滤
        if (deptId != null) {
            List<Long> deptEmpIds = hrEmployeeMapper.selectList(
                    new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getDeptId, deptId)
            ).stream().map(HrEmployee::getId).collect(Collectors.toList());
            if (deptEmpIds.isEmpty()) {
                // 该部门无员工，返回空结果
                PageResult<PerfRecordVO> empty = new PageResult<>();
                empty.setTotal(0L);
                empty.setPage(page != null ? page : 1);
                empty.setPageSize(pageSize != null ? pageSize : 10);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.in(PerfRecord::getEmployeeId, deptEmpIds);
        }
        wrapper.orderByDesc(PerfRecord::getCreateTime);

        Page<PerfRecord> pg = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        Page<PerfRecord> result = perfRecordMapper.selectPage(pg, wrapper);

        List<PerfRecordVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        PageResult<PerfRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage((int) pg.getCurrent());
        pageResult.setPageSize((int) pg.getSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public List<Map<String, Object>> deptSummary(Long planId) {
        // 查询所有考核记录（按计划过滤）
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(PerfRecord::getPlanId, planId);
        }
        List<PerfRecord> records = perfRecordMapper.selectList(wrapper);

        if (records.isEmpty()) {
            return Collections.emptyList();
        }

        // 获取所有相关员工ID，批量查询员工和部门
        List<Long> empIds = records.stream()
                .map(PerfRecord::getEmployeeId).distinct().collect(Collectors.toList());
        Map<Long, HrEmployee> empMap = hrEmployeeMapper.selectBatchIds(empIds).stream()
                .collect(Collectors.toMap(HrEmployee::getId, e -> e));

        // 批量查部门
        List<Long> deptIds = empMap.values().stream()
                .map(HrEmployee::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Collections.emptyMap()
                : sysDeptMapper.selectBatchIds(deptIds).stream()
                .collect(Collectors.toMap(SysDept::getId, SysDept::getName));

        // 批量查等级，用于判断"优秀"
        List<Long> levelIds = records.stream()
                .map(PerfRecord::getLevelId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> levelNameMap = levelIds.isEmpty() ? Collections.emptyMap()
                : perfLevelMapper.selectBatchIds(levelIds).stream()
                .collect(Collectors.toMap(PerfLevel::getId, PerfLevel::getName));

        // 按部门分组统计
        Map<Long, List<PerfRecord>> deptGroup = records.stream()
                .filter(r -> {
                    HrEmployee emp = empMap.get(r.getEmployeeId());
                    return emp != null && emp.getDeptId() != null;
                })
                .collect(Collectors.groupingBy(r -> empMap.get(r.getEmployeeId()).getDeptId()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Long, List<PerfRecord>> entry : deptGroup.entrySet()) {
            Long deptId = entry.getKey();
            List<PerfRecord> deptRecords = entry.getValue();

            // 去重员工数
            long totalCount = deptRecords.stream()
                    .map(PerfRecord::getEmployeeId).distinct().count();

            // 平均分
            BigDecimal avgScore = deptRecords.stream()
                    .map(PerfRecord::getTotalScore)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(deptRecords.size()), 2, RoundingMode.HALF_UP);

            // 优秀人数（等级为 S 或 A）
            long excellentCount = deptRecords.stream()
                    .filter(r -> {
                        String levelName = levelNameMap.get(r.getLevelId());
                        return "S".equals(levelName) || "A".equals(levelName);
                    }).count();

            Map<String, Object> deptSummary = new LinkedHashMap<>();
            deptSummary.put("deptId", deptId);
            deptSummary.put("deptName", deptNameMap.getOrDefault(deptId, "未知部门"));
            deptSummary.put("totalCount", totalCount);
            deptSummary.put("avgScore", avgScore);
            deptSummary.put("excellentCount", excellentCount);
            result.add(deptSummary);
        }

        // 按平均分降序排列
        result.sort((a, b) -> {
            BigDecimal aScore = (BigDecimal) a.get("avgScore");
            BigDecimal bScore = (BigDecimal) b.get("avgScore");
            return bScore.compareTo(aScore);
        });

        return result;
    }

    @Override
    public PageResult<PerfRecordVO> employeeSummary(Long planId, Long deptId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(PerfRecord::getPlanId, planId);
        }

        // deptId 过滤
        List<Long> deptEmpIds = null;
        if (deptId != null) {
            deptEmpIds = hrEmployeeMapper.selectList(
                    new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getDeptId, deptId)
            ).stream().map(HrEmployee::getId).collect(Collectors.toList());
            if (deptEmpIds.isEmpty()) {
                PageResult<PerfRecordVO> empty = new PageResult<>();
                empty.setTotal(0L);
                empty.setPage(page != null ? page : 1);
                empty.setPageSize(pageSize != null ? pageSize : 10);
                empty.setRecords(Collections.emptyList());
                return empty;
            }
            wrapper.in(PerfRecord::getEmployeeId, deptEmpIds);
        }

        // 按总分降序排列
        wrapper.orderByDesc(PerfRecord::getTotalScore);

        Page<PerfRecord> pg = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        Page<PerfRecord> result = perfRecordMapper.selectPage(pg, wrapper);

        List<PerfRecordVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        // 计算排名（基于当前页在全量中的偏移）
        int rankOffset = (int) ((pg.getCurrent() - 1) * pg.getSize());
        for (int i = 0; i < voList.size(); i++) {
            voList.get(i).setRank(rankOffset + i + 1);
        }

        PageResult<PerfRecordVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage(page != null ? page : 1);
        pageResult.setPageSize(pageSize != null ? pageSize : 10);
        pageResult.setRecords(voList);
        return pageResult;
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
