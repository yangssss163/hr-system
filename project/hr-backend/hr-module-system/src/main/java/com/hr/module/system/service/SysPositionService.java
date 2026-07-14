package com.hr.module.system.service;

import com.hr.module.system.entity.SysPosition;
import com.hr.module.system.dto.PositionVO;

import java.util.List;

public interface SysPositionService {
    List<PositionVO> list(Integer page, Integer pageSize, Long deptId, String keyword);
    Long count(Integer page, Integer pageSize, Long deptId, String keyword);
    SysPosition getById(Long id);
    void create(SysPosition position);
    void update(SysPosition position);
    void delete(Long id);
}
