package com.hr.module.attendance.helper;

import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.mapper.SysDeptMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 考勤模块人员数据填充助手 — 从员工模块和系统模块获取员工姓名/工号/部门信息
 */
@Component
@RequiredArgsConstructor
public class AttendanceEmployeeHelper {

    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;

    /**
     * 批量获取员工信息
     * @param employeeIds 员工ID集合
     * @return Map<employeeId, EmployeeInfo>
     */
    public Map<Long, EmployeeInfo> getEmployeeInfoMap(Collection<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> distinctIds = employeeIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1. 批量查询员工
        List<HrEmployee> employees = hrEmployeeMapper.selectBatchIds(distinctIds);
        if (employees.isEmpty()) {
            return Collections.emptyMap();
        }

        // 2. 收集所有部门ID
        Set<Long> deptIds = employees.stream()
                .map(HrEmployee::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 3. 批量查询部门
        Map<Long, String> deptNameMap = Collections.emptyMap();
        if (!deptIds.isEmpty()) {
            List<SysDept> depts = sysDeptMapper.selectBatchIds(deptIds);
            deptNameMap = depts.stream()
                    .collect(Collectors.toMap(SysDept::getId, SysDept::getName, (a, b) -> a));
        }

        // 4. 构建结果 Map
        Map<Long, EmployeeInfo> result = new HashMap<>();
        for (HrEmployee emp : employees) {
            EmployeeInfo info = new EmployeeInfo();
            info.setEmpNo(emp.getEmpNo());
            info.setEmployeeName(emp.getName());
            info.setDeptId(emp.getDeptId());
            info.setDeptName(deptNameMap.getOrDefault(emp.getDeptId(), ""));
            result.put(emp.getId(), info);
        }
        return result;
    }

    /**
     * 校验员工是否存在
     */
    public boolean employeeExists(Long employeeId) {
        if (employeeId == null) return false;
        return hrEmployeeMapper.selectById(employeeId) != null;
    }

    @Data
    public static class EmployeeInfo {
        private String empNo;
        private String employeeName;
        private Long deptId;
        private String deptName;
    }
}
