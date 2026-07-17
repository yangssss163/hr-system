package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmExpenseDTO;
import com.hr.module.crm.dto.CrmExpenseQuery;
import com.hr.module.crm.dto.CrmExpenseVO;
import com.hr.module.crm.entity.CrmExpense;
import com.hr.module.crm.mapper.CrmExpenseMapper;
import com.hr.module.crm.service.CrmExpenseService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrmExpenseServiceImpl implements CrmExpenseService {

    private final CrmExpenseMapper crmExpenseMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public IPage<CrmExpenseVO> page(CrmExpenseQuery query) {
        LambdaQueryWrapper<CrmExpense> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmExpense::getName, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(CrmExpense::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(CrmExpense::getCategory, query.getCategory());
        }
        if (query.getApplicantId() != null) {
            wrapper.eq(CrmExpense::getApplicantId, query.getApplicantId());
        }
        wrapper.orderByDesc(CrmExpense::getCreateTime);
        Page<CrmExpense> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmExpense> result = crmExpenseMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmExpenseVO getById(Long id) {
        CrmExpense entity = crmExpenseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("费用记录不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmExpenseDTO dto) {
        CrmExpense entity = new CrmExpense();
        entity.setName(dto.getName());
        entity.setAmount(dto.getAmount());
        if (StringUtils.hasText(dto.getExpenseDate())) {
            entity.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
        }
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus());
        entity.setApplicantId(dto.getApplicantId());
        entity.setRemark(dto.getRemark());
        crmExpenseMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmExpenseDTO dto) {
        CrmExpense entity = crmExpenseMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("费用记录不存在");
        }
        entity.setName(dto.getName());
        entity.setAmount(dto.getAmount());
        if (StringUtils.hasText(dto.getExpenseDate())) {
            entity.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
        }
        entity.setCategory(dto.getCategory());
        entity.setStatus(dto.getStatus());
        entity.setApplicantId(dto.getApplicantId());
        entity.setRemark(dto.getRemark());
        crmExpenseMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmExpenseMapper.deleteById(id);
    }

    private CrmExpenseVO toVO(CrmExpense entity) {
        CrmExpenseVO vo = new CrmExpenseVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setAmount(entity.getAmount());
        if (entity.getExpenseDate() != null) {
            vo.setExpenseDate(entity.getExpenseDate().toString());
        }
        vo.setCategory(entity.getCategory());
        vo.setStatus(entity.getStatus());
        vo.setApplicantId(entity.getApplicantId());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        if (entity.getApplicantId() != null) {
            HrEmployee emp = hrEmployeeMapper.selectById(entity.getApplicantId());
            if (emp != null) vo.setApplicantName(emp.getName());
        }
        return vo;
    }
}
