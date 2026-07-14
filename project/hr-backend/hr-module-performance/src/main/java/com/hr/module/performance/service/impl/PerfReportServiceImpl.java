package com.hr.module.performance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public IPage<PerfRecordVO> detail(Long planId, Long deptId, Integer page, Integer pageSize) {
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        if (planId != null) {
            wrapper.eq(PerfRecord::getPlanId, planId);
        }
        wrapper.orderByDesc(PerfRecord::getCreateTime);

        Page<PerfRecord> pg = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        Page<PerfRecord> result = perfRecordMapper.selectPage(pg, wrapper);

        List<PerfRecord> records = result.getRecords();
        if (deptId != null && !records.isEmpty()) {
            List<Long> empIds = records.stream()
                    .map(PerfRecord::getEmployeeId).distinct().collect(Collectors.toList());
            List<Long> deptEmpIds = hrEmployeeMapper.selectBatchIds(empIds).stream()
                    .filter(e -> deptId.equals(e.getDeptId()))
                    .map(HrEmployee::getId).collect(Collectors.toList());
            records = records.stream()
                    .filter(r -> deptEmpIds.contains(r.getEmployeeId()))
                    .collect(Collectors.toList());
        }

        List<PerfRecordVO> voList = records.stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        Page<PerfRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), records.size());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<Map<String, Object>> deptSummary() {
        return Collections.emptyList();
    }

    @Override
    public IPage<PerfRecordVO> employeeSummary(Integer page, Integer pageSize) {
        LambdaQueryWrapper<PerfRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(PerfRecord::getCreateTime);

        Page<PerfRecord> pg = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        Page<PerfRecord> result = perfRecordMapper.selectPage(pg, wrapper);

        List<PerfRecordVO> voList = result.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        Page<PerfRecordVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
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
