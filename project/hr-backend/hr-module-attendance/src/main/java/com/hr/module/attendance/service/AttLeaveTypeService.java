package com.hr.module.attendance.service;

import com.hr.module.attendance.dto.AttLeaveTypeDTO;
import com.hr.module.attendance.dto.AttLeaveTypeVO;

import java.util.List;

public interface AttLeaveTypeService {
    List<AttLeaveTypeVO> list();
    AttLeaveTypeVO getById(Long id);
    void create(AttLeaveTypeDTO dto);
    void update(Long id, AttLeaveTypeDTO dto);
    void delete(Long id);
}
