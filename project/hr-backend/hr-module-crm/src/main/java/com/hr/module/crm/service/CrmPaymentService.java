package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmPaymentDTO;
import com.hr.module.crm.dto.CrmPaymentQuery;
import com.hr.module.crm.dto.CrmPaymentVO;

public interface CrmPaymentService {
    IPage<CrmPaymentVO> page(CrmPaymentQuery query);
    CrmPaymentVO getById(Long id);
    void create(CrmPaymentDTO dto);
    void update(Long id, CrmPaymentDTO dto);
    void delete(Long id);
}
