package com.hr.module.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.salary.dto.SalRuleEditDTO;
import com.hr.module.salary.dto.SalRuleVO;
import com.hr.module.salary.entity.SalRule;
import com.hr.module.salary.mapper.SalRuleMapper;
import com.hr.module.salary.service.SalRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalRuleServiceImpl implements SalRuleService {

    private final SalRuleMapper salRuleMapper;

    @Override
    public List<SalRuleVO> list() {
        List<SalRule> rules = salRuleMapper.selectList(
                new LambdaQueryWrapper<SalRule>().orderByAsc(SalRule::getId));
        return rules.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public void update(Long id, SalRuleEditDTO dto) {
        SalRule rule = salRuleMapper.selectById(id);
        if (rule == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "规则不存在");
        }
        rule.setValue(dto.getValue());
        rule.setRemark(dto.getRemark());
        salRuleMapper.updateById(rule);
    }

    private SalRuleVO toVO(SalRule rule) {
        SalRuleVO vo = new SalRuleVO();
        vo.setId(rule.getId());
        vo.setType(rule.getType());
        vo.setTypeName(getTypeName(rule.getType()));
        vo.setValue(rule.getValue());
        vo.setUnit(rule.getUnit());
        vo.setRemark(rule.getRemark());
        return vo;
    }

    private String getTypeName(String type) {
        if ("base_salary".equals(type)) return "基本工资基数";
        if ("social_insurance".equals(type)) return "社保个人缴纳比例";
        if ("tax_threshold".equals(type)) return "个税起征点";
        if ("tax_rate".equals(type)) return "综合所得税率";
        if ("overtime_rate".equals(type)) return "工作日加班费率";
        if ("overtime_weekend".equals(type)) return "周末加班费率";
        if ("subsidy_default".equals(type)) return "默认岗位补贴";
        if ("pension".equals(type)) return "养老保险个人比例";
        if ("medical".equals(type)) return "医疗保险个人比例";
        if ("unemployment".equals(type)) return "失业保险个人比例";
        if ("housing_fund".equals(type)) return "公积金个人比例";
        return type;
    }
}
