package com.hr.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.framework.security.JwtUtils;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.auth.dto.LoginDTO;
import com.hr.module.auth.service.AuthService;
import com.hr.module.system.dto.MenuTreeVO;
import com.hr.module.system.entity.*;
import com.hr.module.system.mapper.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_PASSWORD_ERROR.getCode(),
                    ResultCode.USERNAME_PASSWORD_ERROR.getMessage());
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_PASSWORD_ERROR.getCode(),
                    ResultCode.USERNAME_PASSWORD_ERROR.getMessage());
        }

        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED.getCode(),
                    ResultCode.USER_DISABLED.getMessage());
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(LocalDateTime.now());
        sysUserMapper.updateById(updateUser);

        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("expiresIn", expiration);
        return result;
    }

    @Override
    public void logout() {
        log.info("用户登出: userId={}", SecurityUtils.getCurrentUserId());
    }

    @Override
    public Map<String, Object> getUserInfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
        }

        // 查询部门名称
        String deptName = null;
        if (user.getDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(user.getDeptId());
            deptName = dept != null ? dept.getName() : null;
        }

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        List<String> roleCodes = Collections.emptyList();
        Set<Long> roleIdSet = new HashSet<>();
        if (!userRoles.isEmpty()) {
            roleIdSet = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());
            List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIdSet);
            roleCodes = roles.stream().map(SysRole::getCode).collect(Collectors.toList());
        }

        // 查询权限标识
        List<String> permissions;
        if (roleIdSet.isEmpty()) {
            permissions = Collections.emptyList();
        } else {
            List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                    new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIdSet));
            Set<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
            if (menuIds.isEmpty()) {
                permissions = Collections.emptyList();
            } else {
                permissions = sysMenuMapper.selectBatchIds(menuIds).stream()
                        .map(SysMenu::getPermission)
                        .filter(StringUtils::hasText)
                        .distinct()
                        .collect(Collectors.toList());
            }
        }

        Map<String, Object> userInfo = new LinkedHashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("deptId", user.getDeptId());
        userInfo.put("deptName", deptName);
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("roles", roleCodes);
        userInfo.put("permissions", permissions);
        return userInfo;
    }

    @Override
    public List<Map<String, Object>> getMenus() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Collections.emptyList();
        }

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());

        // 查询角色关联的菜单
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        Set<Long> menuIds = roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
        if (menuIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询菜单（只返回目录和菜单类型，即 type=1,2）
        List<SysMenu> allMenus = sysMenuMapper.selectBatchIds(menuIds).stream()
                .filter(m -> m.getType() != null && (m.getType() == 1 || m.getType() == 2))
                .sorted(Comparator.comparingInt(m -> m.getSort() != null ? m.getSort() : 0))
                .collect(Collectors.toList());

        Set<Long> menuIdSet = allMenus.stream().map(SysMenu::getId).collect(Collectors.toSet());
        return buildMenuTree(allMenus, 0L, menuIdSet);
    }

    private List<Map<String, Object>> buildMenuTree(List<SysMenu> menus, Long parentId, Set<Long> validIds) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId) && validIds.contains(menu.getId())) {
                Map<String, Object> node = new LinkedHashMap<>();
                node.put("id", menu.getId());
                node.put("parentId", menu.getParentId());
                node.put("name", menu.getName());
                node.put("type", menu.getType());
                node.put("path", menu.getPath());
                node.put("component", menu.getComponent());
                node.put("permission", menu.getPermission());
                node.put("icon", menu.getIcon());
                node.put("sort", menu.getSort());

                List<Map<String, Object>> children = buildMenuTree(menus, menu.getId(), validIds);
                node.put("children", children);
                result.add(node);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "未登录");
        }

        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR.getCode(),
                    ResultCode.OLD_PASSWORD_ERROR.getMessage());
        }

        SysUser updateUser = new SysUser();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        sysUserMapper.updateById(updateUser);

        log.info("密码修改成功: userId={}", userId);
    }
}
