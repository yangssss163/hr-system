package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.system.dto.CompanyTreeVO;
import com.hr.module.system.entity.SysCompany;
import com.hr.module.system.mapper.SysCompanyMapper;
import com.hr.module.system.service.SysCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysCompanyServiceImpl implements SysCompanyService {

    private final SysCompanyMapper sysCompanyMapper;

    @Override
    public List<CompanyTreeVO> tree() {
        List<SysCompany> companies = sysCompanyMapper.selectList(
                new LambdaQueryWrapper<SysCompany>().orderByAsc(SysCompany::getSort));
        return buildTree(companies, 0L);
    }

    private List<CompanyTreeVO> buildTree(List<SysCompany> companies, Long parentId) {
        List<CompanyTreeVO> trees = new ArrayList<>();
        for (SysCompany company : companies) {
            if (company.getParentId().equals(parentId)) {
                CompanyTreeVO vo = new CompanyTreeVO();
                vo.setId(company.getId());
                vo.setName(company.getName());
                vo.setCode(company.getCode());
                vo.setParentId(company.getParentId());
                vo.setSort(company.getSort());
                vo.setStatus(company.getStatus());
                vo.setChildren(buildTree(companies, company.getId()));
                trees.add(vo);
            }
        }
        return trees;
    }

    @Override
    public SysCompany getById(Long id) {
        return sysCompanyMapper.selectById(id);
    }

    @Override
    public void create(SysCompany company) {
        sysCompanyMapper.insert(company);
    }

    @Override
    public void update(SysCompany company) {
        sysCompanyMapper.updateById(company);
    }

    @Override
    public void delete(Long id) {
        Long count = sysCompanyMapper.selectCount(
                new LambdaQueryWrapper<SysCompany>().eq(SysCompany::getParentId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.HAS_ASSOCIATED_DATA.getCode(), "存在子公司，无法删除");
        }
        sysCompanyMapper.deleteById(id);
    }
}
