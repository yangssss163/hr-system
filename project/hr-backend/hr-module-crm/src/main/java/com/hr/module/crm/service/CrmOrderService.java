package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmOrderDTO;
import com.hr.module.crm.dto.CrmOrderQuery;
import com.hr.module.crm.dto.CrmOrderVO;

public interface CrmOrderService {
    IPage<CrmOrderVO> page(CrmOrderQuery query);
    CrmOrderVO getById(Long id);
    void create(CrmOrderDTO dto);
    void update(Long id, CrmOrderDTO dto);
    void delete(Long id);
}
