package com.hr.module.performance.service;

import com.hr.module.performance.dto.PerfSalaryDTO;
import com.hr.module.performance.vo.PerfSalaryVO;

import java.util.List;

public interface PerfSalaryService {
    List<PerfSalaryVO> list();
    PerfSalaryVO getById(Long id);
    void create(PerfSalaryDTO dto);
    void update(Long id, PerfSalaryDTO dto);
    void delete(Long id);
}
