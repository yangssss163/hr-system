package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmRefundDTO;
import com.hr.module.crm.dto.CrmRefundQuery;
import com.hr.module.crm.dto.CrmRefundVO;

public interface CrmRefundService {
    IPage<CrmRefundVO> page(CrmRefundQuery query);
    CrmRefundVO getById(Long id);
    void create(CrmRefundDTO dto);
    void update(Long id, CrmRefundDTO dto);
    void delete(Long id);
}
