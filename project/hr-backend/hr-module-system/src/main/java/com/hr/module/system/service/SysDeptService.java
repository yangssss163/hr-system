package com.hr.module.system.service;

import com.hr.module.system.dto.DeptTreeVO;
import com.hr.module.system.entity.SysDept;

import java.util.List;

public interface SysDeptService {
    List<DeptTreeVO> tree(Long companyId);
    SysDept getById(Long id);
    void create(SysDept dept);
    void update(SysDept dept);
    void delete(Long id);
}
