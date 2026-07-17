package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmOrderDTO;
import com.hr.module.crm.dto.CrmOrderQuery;
import com.hr.module.crm.dto.CrmOrderVO;
import com.hr.module.crm.entity.CrmOrder;
import com.hr.module.crm.entity.CrmCustomer;
import com.hr.module.crm.mapper.CrmOrderMapper;
import com.hr.module.crm.mapper.CrmCustomerMapper;
import com.hr.module.crm.service.CrmOrderService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrmOrderServiceImpl implements CrmOrderService {

    private final CrmOrderMapper crmOrderMapper;
    private final CrmCustomerMapper crmCustomerMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public IPage<CrmOrderVO> page(CrmOrderQuery query) {
        LambdaQueryWrapper<CrmOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmOrder::getOrderNo, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(CrmOrder::getStatus, query.getStatus());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(CrmOrder::getCustomerId, query.getCustomerId());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(CrmOrder::getOwnerId, query.getOwnerId());
        }
        wrapper.orderByDesc(CrmOrder::getCreateTime);
        Page<CrmOrder> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmOrder> result = crmOrderMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmOrderVO getById(Long id) {
        CrmOrder entity = crmOrderMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("订单不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmOrderDTO dto) {
        CrmOrder entity = new CrmOrder();
        entity.setOpportunityId(dto.getOpportunityId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setOrderNo(dto.getOrderNo());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getSignDate())) {
            entity.setSignDate(LocalDate.parse(dto.getSignDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setRemark(dto.getRemark());
        crmOrderMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmOrderDTO dto) {
        CrmOrder entity = crmOrderMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("订单不存在");
        }
        entity.setOpportunityId(dto.getOpportunityId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setOrderNo(dto.getOrderNo());
        entity.setAmount(dto.getAmount());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getSignDate())) {
            entity.setSignDate(LocalDate.parse(dto.getSignDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setRemark(dto.getRemark());
        crmOrderMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmOrderMapper.deleteById(id);
    }

    private CrmOrderVO toVO(CrmOrder entity) {
        CrmOrderVO vo = new CrmOrderVO();
        vo.setId(entity.getId());
        vo.setOpportunityId(entity.getOpportunityId());
        vo.setCustomerId(entity.getCustomerId());
        vo.setOrderNo(entity.getOrderNo());
        vo.setAmount(entity.getAmount());
        vo.setStatus(entity.getStatus());
        if (entity.getSignDate() != null) {
            vo.setSignDate(entity.getSignDate().toString());
        }
        vo.setOwnerId(entity.getOwnerId());
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
