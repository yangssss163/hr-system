package com.hr.module.performance.service;

import com.hr.common.result.PageResult;
import com.hr.module.performance.dto.PerfPlanDTO;
import com.hr.module.performance.query.PerfPlanQuery;
import com.hr.module.performance.vo.PerfPlanVO;

public interface PerfPlanService {
    PageResult<PerfPlanVO> page(PerfPlanQuery query);
    PerfPlanVO getById(Long id);
    void create(PerfPlanDTO dto);
    void update(Long id, PerfPlanDTO dto);
    void delete(Long id);
}
