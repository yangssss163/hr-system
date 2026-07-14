package com.hr.module.performance.service;

import com.hr.module.performance.dto.PerfLevelDTO;
import com.hr.module.performance.vo.PerfLevelVO;

import java.util.List;

public interface PerfLevelService {
    List<PerfLevelVO> list();
    PerfLevelVO getById(Long id);
    void create(PerfLevelDTO dto);
    void update(Long id, PerfLevelDTO dto);
    void delete(Long id);
}
