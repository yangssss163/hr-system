package com.hr.module.system.service;

import com.hr.module.system.dto.MenuTreeVO;
import com.hr.module.system.entity.SysMenu;

import java.util.List;

public interface SysMenuService {
    List<MenuTreeVO> tree();
    SysMenu getById(Long id);
    void create(SysMenu menu);
    void update(SysMenu menu);
    void delete(Long id);
}
