package com.hr.module.attendance.service;

import com.hr.module.attendance.dto.AttDeductionEditDTO;
import com.hr.module.attendance.dto.AttDeductionVO;

import java.util.List;

public interface AttDeductionService {
    List<AttDeductionVO> list();
    void update(Long id, AttDeductionEditDTO dto);
}
