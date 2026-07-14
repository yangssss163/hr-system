package com.hr.module.salary.service;

import com.hr.module.salary.dto.SalRuleEditDTO;
import com.hr.module.salary.dto.SalRuleVO;

import java.util.List;

public interface SalRuleService {
    List<SalRuleVO> list();
    void update(Long id, SalRuleEditDTO dto);
}
