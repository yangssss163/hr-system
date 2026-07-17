package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmOpportunityDTO;
import com.hr.module.crm.dto.CrmOpportunityQuery;
import com.hr.module.crm.dto.CrmOpportunityVO;
import com.hr.module.crm.entity.CrmOpportunity;
import com.hr.module.crm.entity.CrmCustomer;
import com.hr.module.crm.mapper.CrmOpportunityMapper;
import com.hr.module.crm.mapper.CrmCustomerMapper;
import com.hr.module.crm.service.CrmOpportunityService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrmOpportunityServiceImpl implements CrmOpportunityService {

    private final CrmOpportunityMapper crmOpportunityMapper;
    private final CrmCustomerMapper crmCustomerMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public IPage<CrmOpportunityVO> page(CrmOpportunityQuery query) {
        LambdaQueryWrapper<CrmOpportunity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmOpportunity::getName, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStage())) {
            wrapper.eq(CrmOpportunity::getStage, query.getStage());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(CrmOpportunity::getOwnerId, query.getOwnerId());
        }
        wrapper.orderByDesc(CrmOpportunity::getCreateTime);
        Page<CrmOpportunity> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmOpportunity> result = crmOpportunityMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmOpportunityVO getById(Long id) {
        CrmOpportunity entity = crmOpportunityMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("商机不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmOpportunityDTO dto) {
        CrmOpportunity entity = new CrmOpportunity();
        entity.setName(dto.getName());
        entity.setCustomerId(dto.getCustomerId());
        entity.setAmount(dto.getAmount());
        entity.setStage(dto.getStage());
        entity.setProbability(dto.getProbability());
        if (StringUtils.hasText(dto.getExpectedCloseDate())) {
            entity.setExpectedCloseDate(LocalDate.parse(dto.getExpectedCloseDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setRemark(dto.getRemark());
        crmOpportunityMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmOpportunityDTO dto) {
        CrmOpportunity entity = crmOpportunityMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("商机不存在");
        }
        entity.setName(dto.getName());
        entity.setCustomerId(dto.getCustomerId());
        entity.setAmount(dto.getAmount());
        entity.setStage(dto.getStage());
        entity.setProbability(dto.getProbability());
        if (StringUtils.hasText(dto.getExpectedCloseDate())) {
            entity.setExpectedCloseDate(LocalDate.parse(dto.getExpectedCloseDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setRemark(dto.getRemark());
        crmOpportunityMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmOpportunityMapper.deleteById(id);
    }

    private CrmOpportunityVO toVO(CrmOpportunity entity) {
        CrmOpportunityVO vo = new CrmOpportunityVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setCustomerId(entity.getCustomerId());
        vo.setAmount(entity.getAmount());
        vo.setStage(entity.getStage());
        vo.setProbability(entity.getProbability());
        if (entity.getExpectedCloseDate() != null) {
            vo.setExpectedCloseDate(entity.getExpectedCloseDate().toString());
        }
        vo.setOwnerId(entity.getOwnerId());
        vo.setContactName(entity.getContactName());
        vo.setContactPhone(entity.getContactPhone());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        if (entity.getCustomerId() != null) {
            CrmCustomer customer = crmCustomerMapper.selectById(entity.getCustomerId());
            if (customer != null) vo.setCustomerName(customer.getName());
        }
        if (entity.getOwnerId() != null) {
            HrEmployee emp = hrEmployeeMapper.selectById(entity.getOwnerId());
            if (emp != null) vo.setOwnerName(emp.getName());
        }
        return vo;
    }
}
