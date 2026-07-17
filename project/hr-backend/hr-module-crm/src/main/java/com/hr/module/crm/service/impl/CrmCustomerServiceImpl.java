package com.hr.module.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.crm.dto.CrmCustomerDTO;
import com.hr.module.crm.dto.CrmCustomerQuery;
import com.hr.module.crm.dto.CrmCustomerVO;
import com.hr.module.crm.entity.CrmCustomer;
import com.hr.module.crm.mapper.CrmCustomerMapper;
import com.hr.module.crm.service.CrmCustomerService;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CrmCustomerServiceImpl implements CrmCustomerService {

    private final CrmCustomerMapper crmCustomerMapper;
    private final HrEmployeeMapper hrEmployeeMapper;

    @Override
    public IPage<CrmCustomerVO> page(CrmCustomerQuery query) {
        LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(CrmCustomer::getName, query.getKeyword())
                   .or().like(CrmCustomer::getPhone, query.getKeyword());
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(CrmCustomer::getStatus, query.getStatus());
        }
        if (query.getOwnerId() != null) {
            wrapper.eq(CrmCustomer::getOwnerId, query.getOwnerId());
        }
        wrapper.orderByDesc(CrmCustomer::getCreateTime);
        Page<CrmCustomer> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<CrmCustomer> result = crmCustomerMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public CrmCustomerVO getById(Long id) {
        CrmCustomer entity = crmCustomerMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("客户不存在");
        }
        return toVO(entity);
    }

    @Override
    public void create(CrmCustomerDTO dto) {
        CrmCustomer entity = new CrmCustomer();
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setIndustry(dto.getIndustry());
        entity.setSource(dto.getSource());
        entity.setLevel(dto.getLevel());
        entity.setStatus(dto.getStatus());
        entity.setOwnerId(dto.getOwnerId());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setRemark(dto.getRemark());
        crmCustomerMapper.insert(entity);
    }

    @Override
    public void update(Long id, CrmCustomerDTO dto) {
        CrmCustomer entity = crmCustomerMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException("客户不存在");
        }
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setIndustry(dto.getIndustry());
        entity.setSource(dto.getSource());
        entity.setLevel(dto.getLevel());
        entity.setStatus(dto.getStatus());
        entity.setOwnerId(dto.getOwnerId());
        entity.setProvince(dto.getProvince());
        entity.setCity(dto.getCity());
        entity.setAddress(dto.getAddress());
        entity.setContactName(dto.getContactName());
        entity.setContactPhone(dto.getContactPhone());
        entity.setRemark(dto.getRemark());
        crmCustomerMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        crmCustomerMapper.deleteById(id);
    }

    private CrmCustomerVO toVO(CrmCustomer entity) {
        CrmCustomerVO vo = new CrmCustomerVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPhone(entity.getPhone());
        vo.setEmail(entity.getEmail());
        vo.setIndustry(entity.getIndustry());
        vo.setSource(entity.getSource());
        vo.setLevel(entity.getLevel());
        vo.setStatus(entity.getStatus());
        vo.setOwnerId(entity.getOwnerId());
        vo.setProvince(entity.getProvince());
        vo.setCity(entity.getCity());
        vo.setAddress(entity.getAddress());
        vo.setContactName(entity.getContactName());
        vo.setContactPhone(entity.getContactPhone());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        if (entity.getOwnerId() != null) {
            HrEmployee emp = hrEmployeeMapper.selectById(entity.getOwnerId());
            if (emp != null) vo.setOwnerName(emp.getName());
        }
        return vo;
    }
}
