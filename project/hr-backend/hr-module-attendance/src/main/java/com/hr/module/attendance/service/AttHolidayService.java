package com.hr.module.attendance.service;

import com.hr.module.attendance.dto.AttHolidayDTO;
import com.hr.module.attendance.dto.AttHolidayVO;

public interface AttHolidayService {
    AttHolidayVO list(Integer year);
    void create(AttHolidayDTO dto);
    void update(Long id, AttHolidayDTO dto);
    void delete(Long id);
    void copy(Long id, Integer targetYear);
}
