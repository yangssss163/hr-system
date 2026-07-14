package com.hr.module.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.framework.security.UserAuthorityProvider;
import com.hr.module.system.entity.SysMenu;
import com.hr.module.system.entity.SysRoleMenu;
import com.hr.module.system.entity.SysUserRole;
import com.hr.module.system.mapper.SysMenuMapper;
import com.hr.module.system.mapper.SysRoleMenuMapper;
import com.hr.module.system.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserAuthorityProviderImpl implements UserAuthorityProvider {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<String> getAuthorities(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        // 查询用户角色
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toSet());

        // 查询角色关联的菜单权限
        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
        if (roleMenus.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = roleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toSet());

        // 查询权限标识
        return sysMenuMapper.selectBatchIds(menuIds).stream()
                .map(SysMenu::getPermission)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
    }
}
