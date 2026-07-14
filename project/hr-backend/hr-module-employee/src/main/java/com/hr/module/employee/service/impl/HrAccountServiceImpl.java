package com.hr.module.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.employee.dto.AccountDTO;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.employee.service.HrAccountService;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.entity.SysUserRole;
import com.hr.module.system.mapper.SysUserMapper;
import com.hr.module.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HrAccountServiceImpl implements HrAccountService {

    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void openAccount(AccountDTO dto) {
        HrEmployee employee = hrEmployeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "员工不存在");
        }
        if (employee.getUserId() != null) {
            throw new BusinessException("该员工已开通账号");
        }

        // 检查用户名唯一性
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "用户名已存在");
        }

        // 创建系统用户
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(employee.getName());
        user.setDeptId(employee.getDeptId());
        user.setPhone(employee.getPhone());
        user.setEmail(employee.getEmail());
        user.setStatus(1);
        sysUserMapper.insert(user);

        // 保存角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            for (Long roleId : dto.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                sysUserRoleMapper.insert(ur);
            }
        }

        // 关联员工和用户
        employee.setUserId(user.getId());
        hrEmployeeMapper.updateById(employee);
    }

    @Override
    @Transactional
    public void toggleAccount(Long id) {
        HrEmployee employee = hrEmployeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "员工不存在");
        }
        if (employee.getUserId() == null) {
            throw new BusinessException("该员工尚未开通账号");
        }

        SysUser user = sysUserMapper.selectById(employee.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "账号不存在");
        }

        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        sysUserMapper.updateById(user);
    }
}
