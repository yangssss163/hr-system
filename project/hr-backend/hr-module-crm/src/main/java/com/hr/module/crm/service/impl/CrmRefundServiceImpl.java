package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmRefundDTO;
import com.hr.module.crm.dto.CrmRefundQuery;
import com.hr.module.crm.dto.CrmRefundVO;
import com.hr.module.crm.entity.CrmRefund;
import com.hr.module.crm.entity.CrmCustomer;
import com.hr.module.crm.mapper.CrmRefundMapper;
import com.hr.module.crm.mapper.CrmCustomerMapper;
import com.hr.module.crm.service.CrmRefundService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrmRefundServiceImpl implements CrmRefundService {

    private final CrmRefundMapper crmRefundMapper;
    private final CrmCustomerMapper crmCustomerMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public IPage<CrmRefundVO> page(CrmRefundQuery query) {
        LambdaQueryWrapper<CrmRefund> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmRefund::getRefundNo, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(CrmRefund::getStatus, query.getStatus());
        }
        if (query.getOrderId() != null) {
            wrapper.eq(CrmRefund::getOrderId, query.getOrderId());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(CrmRefund::getCustomerId, query.getCustomerId());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(CrmRefund::getOwnerId, query.getOwnerId());
        }
        wrapper.orderByDesc(CrmRefund::getCreateTime);
        Page<CrmRefund> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmRefund> result = crmRefundMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmRefundVO getById(Long id) {
        CrmRefund entity = crmRefundMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("退款记录不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmRefundDTO dto) {
        CrmRefund entity = new CrmRefund();
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setRefundNo(dto.getRefundNo());
        entity.setAmount(dto.getAmount());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getRefundDate())) {
            entity.setRefundDate(LocalDate.parse(dto.getRefundDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        crmRefundMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmRefundDTO dto) {
        CrmRefund entity = crmRefundMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("退款记录不存在");
        }
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setRefundNo(dto.getRefundNo());
        entity.setAmount(dto.getAmount());
        entity.setReason(dto.getReason());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getRefundDate())) {
            entity.setRefundDate(LocalDate.parse(dto.getRefundDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        crmRefundMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmRefundMapper.deleteById(id);
    }

    private CrmRefundVO toVO(CrmRefund entity) {
        CrmRefundVO vo = new CrmRefundVO();
        vo.setId(entity.getId());
        vo.setOrderId(entity.getOrderId());
        vo.setCustomerId(entity.getCustomerId());
        vo.setRefundNo(entity.getRefundNo());
        vo.setAmount(entity.getAmount());
        if (entity.getRefundDate() != null) {
            vo.setRefundDate(entity.getRefundDate().toString());
        }
        vo.setReason(entity.getReason());
        vo.setStatus(entity.getStatus());
        vo.setOwnerId(entity.getOwnerId());
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
