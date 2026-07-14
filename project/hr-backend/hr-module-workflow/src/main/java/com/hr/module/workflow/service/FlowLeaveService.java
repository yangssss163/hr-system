package com.hr.module.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLeaveDTO;
import com.hr.module.workflow.dto.FlowLeaveQuery;
import com.hr.module.workflow.dto.FlowLeaveVO;

public interface FlowLeaveService {
    IPage<FlowLeaveVO> page(FlowLeaveQuery query);
    FlowLeaveVO getById(Long id);
    void create(FlowLeaveDTO dto);
    void update(Long id, FlowLeaveDTO dto);
    void delete(Long id);
    void approve(FlowApproveDTO dto);
}
