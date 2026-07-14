package com.hr.module.attendance.service;

import com.hr.module.attendance.dto.AttShiftDTO;
import com.hr.module.attendance.dto.AttShiftVO;

import java.util.List;

public interface AttShiftService {
    List<AttShiftVO> list();
    AttShiftVO getById(Long id);
    void create(AttShiftDTO dto);
    void update(Long id, AttShiftDTO dto);
    void delete(Long id);
}
