package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmOpportunityDTO;
import com.hr.module.crm.dto.CrmOpportunityQuery;
import com.hr.module.crm.dto.CrmOpportunityVO;

public interface CrmOpportunityService {
    IPage<CrmOpportunityVO> page(CrmOpportunityQuery query);
    CrmOpportunityVO getById(Long id);
    void create(CrmOpportunityDTO dto);
    void update(Long id, CrmOpportunityDTO dto);
    void delete(Long id);
}
