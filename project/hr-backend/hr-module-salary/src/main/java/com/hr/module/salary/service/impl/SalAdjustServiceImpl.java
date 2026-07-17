package com.hr.module.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.salary.dto.SalAdjustDTO;
import com.hr.module.salary.dto.SalAdjustQuery;
import com.hr.module.salary.dto.SalAdjustVO;
import com.hr.module.salary.entity.SalAdjust;
import com.hr.module.salary.mapper.SalAdjustMapper;
import com.hr.module.salary.service.SalAdjustService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SalAdjustServiceImpl implements SalAdjustService {

    private final SalAdjustMapper salAdjustMapper;

    @Override
    public IPage<SalAdjustVO> page(SalAdjustQuery query) {
        LambdaQueryWrapper<SalAdjust> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(SalAdjust::getEmployeeId, query.getEmployeeId());
        }
        wrapper.orderByDesc(SalAdjust::getCreateTime);
        Page<SalAdjust> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SalAdjust> result = salAdjustMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public SalAdjustVO getById(Long id) {
        SalAdjust adjust = salAdjustMapper.selectById(id);
        if (adjust == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "调薪记录不存在");
        }
        return toVO(adjust);
    }

    @Override
    public void create(SalAdjustDTO dto) {
        SalAdjust adjust = new SalAdjust();
        adjust.setEmployeeId(dto.getEmployeeId());
        adjust.setAfterSalary(dto.getAfterSalary());
        adjust.setBeforeSalary(BigDecimal.ZERO);
        adjust.setAdjustAmount(dto.getAfterSalary());
        adjust.setAdjustType(dto.getAdjustType());
        adjust.setEffectiveDate(LocalDate.parse(dto.getEffectiveDate()));
        adjust.setRemark(dto.getRemark());
        salAdjustMapper.insert(adjust);
    }

    private SalAdjustVO toVO(SalAdjust adjust) {
        SalAdjustVO vo = new SalAdjustVO();
        vo.setId(adjust.getId());
        vo.setEmployeeId(adjust.getEmployeeId());
        vo.setBeforeSalary(adjust.getBeforeSalary());
        vo.setAfterSalary(adjust.getAfterSalary());
        vo.setAdjustAmount(adjust.getAdjustAmount());
        vo.setAdjustType(adjust.getAdjustType());
        vo.setEffectiveDate(adjust.getEffectiveDate() != null ? adjust.getEffectiveDate().toString() : null);
        vo.setRemark(adjust.getRemark());
        vo.setCreateTime(adjust.getCreateTime());
        return vo;
    }
}
