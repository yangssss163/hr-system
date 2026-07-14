package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.system.dto.RoleSimpleVO;
import com.hr.module.system.dto.RoleVO;
import com.hr.module.system.entity.SysRole;
import com.hr.module.system.entity.SysRoleMenu;
import com.hr.module.system.entity.SysUserRole;
import com.hr.module.system.mapper.SysRoleMapper;
import com.hr.module.system.mapper.SysRoleMenuMapper;
import com.hr.module.system.mapper.SysUserRoleMapper;
import com.hr.module.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public Page<SysRole> page(Integer page, Integer pageSize, String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysRole::getName, keyword).or().like(SysRole::getCode, keyword);
        }
        wrapper.orderByDesc(SysRole::getCreateTime);
        Page<SysRole> p = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        return sysRoleMapper.selectPage(p, wrapper);
    }

    @Override
    public List<RoleSimpleVO> all() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getSort))
                .stream().map(role -> {
                    RoleSimpleVO vo = new RoleSimpleVO();
                    vo.setId(role.getId());
                    vo.setName(role.getName());
                    vo.setCode(role.getCode());
                    return vo;
                }).collect(Collectors.toList());
    }

    @Override
    public RoleVO getById(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) return null;

        RoleVO vo = new RoleVO();
        vo.setId(role.getId());
        vo.setName(role.getName());
        vo.setCode(role.getCode());
        vo.setDescription(role.getDescription());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());

        List<SysRoleMenu> roleMenus = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        vo.setMenuIds(roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public void create(SysRole role) {
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, role.getCode()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "角色编码已存在");
        }
        sysRoleMapper.insert(role);
    }

    @Override
    public void update(SysRole role) {
        Long count = sysRoleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getCode, role.getCode())
                        .ne(SysRole::getId, role.getId()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "角色编码已存在");
        }
        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 检查是否有用户关联
        Long userCount = sysUserRoleMapper.selectCount(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        if (userCount > 0) {
            throw new BusinessException(ResultCode.HAS_ASSOCIATED_DATA.getCode(), "存在关联用户，无法删除");
        }
        // 删除角色菜单关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
        sysRoleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        if (menuIds != null) {
            for (Long menuId : menuIds) {
                SysRoleMenu rm = new SysRoleMenu();
                rm.setRoleId(roleId);
                rm.setMenuId(menuId);
                sysRoleMenuMapper.insert(rm);
            }
        }
    }
}
