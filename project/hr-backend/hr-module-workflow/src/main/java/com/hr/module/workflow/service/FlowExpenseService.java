package com.hr.module.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowExpenseDTO;
import com.hr.module.workflow.dto.FlowExpenseQuery;
import com.hr.module.workflow.dto.FlowExpenseVO;

public interface FlowExpenseService {
    IPage<FlowExpenseVO> page(FlowExpenseQuery query);
    FlowExpenseVO getById(Long id);
    void create(FlowExpenseDTO dto);
    void update(Long id, FlowExpenseDTO dto);
    void delete(Long id);
    void approve(FlowApproveDTO dto);
}
