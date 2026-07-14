package com.hr.module.auth.service;

import com.hr.module.auth.dto.LoginDTO;

import java.util.Map;

public interface AuthService {

    /**
     * 用户登录
     */
    Map<String, Object> login(LoginDTO dto);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户信息
     */
    Map<String, Object> getUserInfo();

    /**
     * 获取当前用户菜单
     */
    Object getMenus();

    /**
     * 修改密码
     */
    void updatePassword(String oldPassword, String newPassword);
}
