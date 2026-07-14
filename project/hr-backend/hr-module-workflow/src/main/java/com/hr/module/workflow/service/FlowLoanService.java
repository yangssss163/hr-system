package com.hr.module.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLoanDTO;
import com.hr.module.workflow.dto.FlowLoanQuery;
import com.hr.module.workflow.dto.FlowLoanVO;

public interface FlowLoanService {
    IPage<FlowLoanVO> page(FlowLoanQuery query);
    FlowLoanVO getById(Long id);
    void create(FlowLoanDTO dto);
    void update(Long id, FlowLoanDTO dto);
    void delete(Long id);
    void approve(FlowApproveDTO dto);
}
