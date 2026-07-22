package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.common.result.PageResult;
import com.hr.module.system.dto.RoleSimpleVO;
import com.hr.module.system.dto.UserDTO;
import com.hr.module.system.dto.UserQuery;
import com.hr.module.system.dto.UserVO;
import com.hr.module.system.entity.*;
import com.hr.module.system.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements com.hr.module.system.service.SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(UserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(SysUser::getUsername, query.getKeyword())
                    .or().like(SysUser::getRealName, query.getKeyword()));
        }
        if (query.getDeptId() != null) {
            wrapper.eq(SysUser::getDeptId, query.getDeptId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> page = new Page<>(query.getPage(), query.getPageSize());
        Page<SysUser> userPage = sysUserMapper.selectPage(page, wrapper);

        // 批量获取部门名称
        List<Long> deptIds = userPage.getRecords().stream()
                .map(SysUser::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));

        // 批量获取用户角色
        List<Long> userIds = userPage.getRecords().stream()
                .map(SysUser::getId).collect(Collectors.toList());
        Map<Long, List<RoleSimpleVO>> userRoleMap = getUserRoleMap(userIds);

        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setDeptId(user.getDeptId());
            vo.setDeptName(deptNameMap.getOrDefault(user.getDeptId(), ""));
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setAvatar(user.getAvatar());
            vo.setStatus(user.getStatus());
            vo.setLastLoginTime(user.getLastLoginTime());
            vo.setCreateTime(user.getCreateTime());
            vo.setRoles(userRoleMap.getOrDefault(user.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(userPage.getTotal());
        result.setPage((int) userPage.getCurrent());
        result.setPageSize((int) userPage.getSize());
        result.setRecords(voList);
        return result;
    }

    private Map<Long, List<RoleSimpleVO>> getUserRoleMap(List<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
        if (userRoles.isEmpty()) return Map.of();

        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
        Map<Long, SysRole> roleMap = sysRoleMapper.selectBatchIds(roleIds).stream()
                .collect(Collectors.toMap(SysRole::getId, r -> r));

        Map<Long, List<RoleSimpleVO>> result = new HashMap<>();
        for (SysUserRole ur : userRoles) {
            SysRole role = roleMap.get(ur.getRoleId());
            if (role != null) {
                RoleSimpleVO vo = new RoleSimpleVO();
                vo.setId(role.getId());
                vo.setName(role.getName());
                vo.setCode(role.getCode());
                result.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(vo);
            }
        }
        return result;
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) return null;

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setDeptId(user.getDeptId());
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(user.getDeptId());
            vo.setDeptName(dept != null ? dept.getName() : "");
        }
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());

        List<Long> ids = Collections.singletonList(id);
        vo.setRoles(getUserRoleMap(ids).getOrDefault(id, Collections.emptyList()));
        return vo;
    }

    @Override
    @Transactional
    public void create(UserDTO dto) {
        // 检查用户名唯一性
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setDeptId(dto.getDeptId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        user.setAvatar(dto.getAvatar());
        sysUserMapper.insert(user);

        // 保存角色关联
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            saveUserRoles(user.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void update(Long id, UserDTO dto) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "用户不存在");
        }

        // 检查用户名唯一性（排除自身）
        Long count = sysUserMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
                        .ne(SysUser::getId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "用户名已存在");
        }

        user.setUsername(dto.getUsername());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRealName(dto.getRealName());
        user.setDeptId(dto.getDeptId());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(dto.getStatus());
        user.setAvatar(dto.getAvatar());
        sysUserMapper.updateById(user);

        // 更新角色关联
        if (dto.getRoleIds() != null) {
            sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
            saveUserRoles(id, dto.getRoleIds());
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 删除用户角色关联
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        sysUserMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(userId, roleIds);
        }
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            sysUserRoleMapper.insert(ur);
        }
    }
}
