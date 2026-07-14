package com.hr.module.salary.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.salary.dto.SalSheetGenerateDTO;
import com.hr.module.salary.dto.SalSheetQuery;
import com.hr.module.salary.dto.SalSheetSyncDTO;
import com.hr.module.salary.dto.SalSheetVO;
import com.hr.module.salary.entity.SalSheet;
import com.hr.module.salary.mapper.SalSheetMapper;
import com.hr.module.salary.service.SalSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalSheetServiceImpl implements SalSheetService {

    private final SalSheetMapper salSheetMapper;

    @Override
    public IPage<SalSheetVO> page(SalSheetQuery query) {
        LambdaQueryWrapper<SalSheet> wrapper = new LambdaQueryWrapper<>();
        if (query.getMonth() != null && !query.getMonth().isEmpty()) {
            wrapper.eq(SalSheet::getMonth, query.getMonth());
        }
        wrapper.orderByDesc(SalSheet::getId);
        Page<SalSheet> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<SalSheet> result = salSheetMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public void sync(SalSheetSyncDTO dto) {
        // stub: 同步绩效考勤数据
    }

    @Override
    public void generate(SalSheetGenerateDTO dto) {
        // stub: 生成工资表
    }

    private SalSheetVO toVO(SalSheet sheet) {
        SalSheetVO vo = new SalSheetVO();
        vo.setId(sheet.getId());
        vo.setEmployeeId(sheet.getEmployeeId());
        vo.setBaseSalary(sheet.getBaseSalary());
        vo.setPerfSalary(sheet.getPerfSalary());
        vo.setSubsidy(sheet.getSubsidy());
        vo.setOvertimePay(sheet.getOvertimePay());
        vo.setTotalIncome(sheet.getTotalIncome());
        vo.setSocialInsurance(sheet.getSocialInsurance());
        vo.setTax(sheet.getTax());
        vo.setTotalDeduction(sheet.getTotalDeduction());
        vo.setNetSalary(sheet.getNetSalary());
        return vo;
    }
}
