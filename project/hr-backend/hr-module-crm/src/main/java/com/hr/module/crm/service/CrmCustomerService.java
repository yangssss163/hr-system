package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmCustomerDTO;
import com.hr.module.crm.dto.CrmCustomerQuery;
import com.hr.module.crm.dto.CrmCustomerVO;

public interface CrmCustomerService {
    IPage<CrmCustomerVO> page(CrmCustomerQuery query);
    CrmCustomerVO getById(Long id);
    void create(CrmCustomerDTO dto);
    void update(Long id, CrmCustomerDTO dto);
    void delete(Long id);
}
