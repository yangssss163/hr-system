package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.system.dto.MenuTreeVO;
import com.hr.module.system.entity.SysMenu;
import com.hr.module.system.mapper.SysMenuMapper;
import com.hr.module.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;

    @Override
    public List<MenuTreeVO> tree() {
        List<SysMenu> menus = sysMenuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getSort));
        return buildTree(menus, 0L);
    }

    private List<MenuTreeVO> buildTree(List<SysMenu> menus, Long parentId) {
        List<MenuTreeVO> trees = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId().equals(parentId)) {
                MenuTreeVO vo = new MenuTreeVO();
                vo.setId(menu.getId());
                vo.setName(menu.getName());
                vo.setParentId(menu.getParentId());
                vo.setSort(menu.getSort());
                vo.setType(menu.getType());
                vo.setPath(menu.getPath());
                vo.setComponent(menu.getComponent());
                vo.setPermission(menu.getPermission());
                vo.setIcon(menu.getIcon());
                if (menu.getVisible() != null) {
                    vo.setVisible(menu.getVisible());
                }
                vo.setChildren(buildTree(menus, menu.getId()));
                trees.add(vo);
            }
        }
        return trees;
    }

    @Override
    public SysMenu getById(Long id) {
        return sysMenuMapper.selectById(id);
    }

    @Override
    public void create(SysMenu menu) {
        sysMenuMapper.insert(menu);
    }

    @Override
    public void update(SysMenu menu) {
        sysMenuMapper.updateById(menu);
    }

    @Override
    public void delete(Long id) {
        Long count = sysMenuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (count > 0) {
            throw new com.hr.common.exception.BusinessException(
                    com.hr.common.enums.ResultCode.HAS_ASSOCIATED_DATA.getCode(),
                    "存在子菜单，无法删除");
        }
        sysMenuMapper.deleteById(id);
    }
}
