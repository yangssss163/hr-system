package com.hr.framework.security;

import java.util.List;

/**
 * 用户权限加载接口
 * 由 hr-module-auth 实现，从数据库加载用户角色对应的权限标识列表
 */
public interface UserAuthorityProvider {

    /**
     * 获取用户的所有权限标识
     * @param userId 用户ID
     * @return 权限标识列表，如 system:user:list, system:user:create 等
     */
    List<String> getAuthorities(Long userId);
}
