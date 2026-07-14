package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmPaymentDTO;
import com.hr.module.crm.dto.CrmPaymentQuery;
import com.hr.module.crm.dto.CrmPaymentVO;
import com.hr.module.crm.entity.CrmPayment;
import com.hr.module.crm.mapper.CrmPaymentMapper;
import com.hr.module.crm.service.CrmPaymentService;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CrmPaymentServiceImpl implements CrmPaymentService {

    private final CrmPaymentMapper crmPaymentMapper;

    @Override
    public IPage<CrmPaymentVO> page(CrmPaymentQuery query) {
        LambdaQueryWrapper<CrmPayment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmPayment::getPaymentNo, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(CrmPayment::getStatus, query.getStatus());
        }
        if (query.getOrderId() != null) {
            wrapper.eq(CrmPayment::getOrderId, query.getOrderId());
        }
        if (query.getCustomerId() != null) {
            wrapper.eq(CrmPayment::getCustomerId, query.getCustomerId());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(CrmPayment::getOwnerId, query.getOwnerId());
        }
        wrapper.orderByDesc(CrmPayment::getCreateTime);
        Page<CrmPayment> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmPayment> result = crmPaymentMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmPaymentVO getById(Long id) {
        CrmPayment entity = crmPaymentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("付款记录不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmPaymentDTO dto) {
        CrmPayment entity = new CrmPayment();
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setPaymentNo(dto.getPaymentNo());
        entity.setAmount(dto.getAmount());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getPaymentDate())) {
            entity.setPaymentDate(LocalDate.parse(dto.getPaymentDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setRemark(dto.getRemark());
        crmPaymentMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmPaymentDTO dto) {
        CrmPayment entity = crmPaymentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("付款记录不存在");
        }
        entity.setOrderId(dto.getOrderId());
        entity.setCustomerId(dto.getCustomerId());
        entity.setPaymentNo(dto.getPaymentNo());
        entity.setAmount(dto.getAmount());
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getPaymentDate())) {
            entity.setPaymentDate(LocalDate.parse(dto.getPaymentDate()));
        }
        entity.setOwnerId(dto.getOwnerId());
        entity.setRemark(dto.getRemark());
        crmPaymentMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmPaymentMapper.deleteById(id);
    }

    private CrmPaymentVO toVO(CrmPayment entity) {
        CrmPaymentVO vo = new CrmPaymentVO();
        vo.setId(entity.getId());
        vo.setOrderId(entity.getOrderId());
        vo.setCustomerId(entity.getCustomerId());
        vo.setPaymentNo(entity.getPaymentNo());
        vo.setAmount(entity.getAmount());
        if (entity.getPaymentDate() != null) {
            vo.setPaymentDate(entity.getPaymentDate().toString());
        }
        vo.setPaymentMethod(entity.getPaymentMethod());
        vo.setStatus(entity.getStatus());
        vo.setOwnerId(entity.getOwnerId());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
