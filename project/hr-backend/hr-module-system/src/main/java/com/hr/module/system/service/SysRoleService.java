package com.hr.module.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.system.entity.SysRole;
import com.hr.module.system.dto.RoleVO;
import com.hr.module.system.dto.RoleSimpleVO;

import java.util.List;

public interface SysRoleService {
    Page<SysRole> page(Integer page, Integer pageSize, String keyword);
    List<RoleSimpleVO> all();
    RoleVO getById(Long id);
    void create(SysRole role);
    void update(SysRole role);
    void delete(Long id);
    void assignMenus(Long roleId, List<Long> menuIds);
}
