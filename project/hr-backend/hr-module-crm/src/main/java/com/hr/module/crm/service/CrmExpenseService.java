package com.hr.module.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.crm.dto.CrmExpenseDTO;
import com.hr.module.crm.dto.CrmExpenseQuery;
import com.hr.module.crm.dto.CrmExpenseVO;

public interface CrmExpenseService {
    IPage<CrmExpenseVO> page(CrmExpenseQuery query);
    CrmExpenseVO getById(Long id);
    void create(CrmExpenseDTO dto);
    void update(Long id, CrmExpenseDTO dto);
    void delete(Long id);
}
