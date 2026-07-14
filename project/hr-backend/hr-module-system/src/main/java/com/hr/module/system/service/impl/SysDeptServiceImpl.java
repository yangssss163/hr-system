package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.system.dto.DeptTreeVO;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.service.SysDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;

    @Override
    public List<DeptTreeVO> tree(Long companyId) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (companyId != null) {
            wrapper.eq(SysDept::getCompanyId, companyId);
        }
        wrapper.orderByAsc(SysDept::getSort);
        List<SysDept> depts = sysDeptMapper.selectList(wrapper);
        return buildTree(depts, 0L);
    }

    private List<DeptTreeVO> buildTree(List<SysDept> depts, Long parentId) {
        List<DeptTreeVO> trees = new ArrayList<>();
        for (SysDept dept : depts) {
            if (dept.getParentId().equals(parentId)) {
                DeptTreeVO vo = new DeptTreeVO();
                vo.setId(dept.getId());
                vo.setName(dept.getName());
                vo.setCompanyId(dept.getCompanyId());
                vo.setParentId(dept.getParentId());
                vo.setSort(dept.getSort());
                vo.setStatus(dept.getStatus());
                vo.setChildren(buildTree(depts, dept.getId()));
                trees.add(vo);
            }
        }
        return trees;
    }

    @Override
    public SysDept getById(Long id) {
        return sysDeptMapper.selectById(id);
    }

    @Override
    public void create(SysDept dept) {
        sysDeptMapper.insert(dept);
    }

    @Override
    public void update(SysDept dept) {
        sysDeptMapper.updateById(dept);
    }

    @Override
    public void delete(Long id) {
        // 检查是否有子部门
        Long count = sysDeptMapper.selectCount(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.HAS_ASSOCIATED_DATA.getCode(), "存在子部门，无法删除");
        }
        sysDeptMapper.deleteById(id);
    }
}
