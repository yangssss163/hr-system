package com.hr.module.workflow.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowTravelDTO;
import com.hr.module.workflow.dto.FlowTravelQuery;
import com.hr.module.workflow.dto.FlowTravelVO;

public interface FlowTravelService {
    IPage<FlowTravelVO> page(FlowTravelQuery query);
    FlowTravelVO getById(Long id);
    void create(FlowTravelDTO dto);
    void update(Long id, FlowTravelDTO dto);
    void delete(Long id);
    void approve(FlowApproveDTO dto);
}
