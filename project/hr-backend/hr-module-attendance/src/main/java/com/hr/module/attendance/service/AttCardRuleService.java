package com.hr.module.attendance.service;

import com.hr.module.attendance.dto.AttCardRuleDTO;
import com.hr.module.attendance.dto.AttCardRuleVO;

import java.util.List;

public interface AttCardRuleService {
    List<AttCardRuleVO> list();
    AttCardRuleVO getById(Long id);
    void create(AttCardRuleDTO dto);
    void update(Long id, AttCardRuleDTO dto);
    void delete(Long id);
}
