package com.hr.module.system.service;

import com.hr.module.system.dto.CompanyTreeVO;
import com.hr.module.system.entity.SysCompany;

import java.util.List;

public interface SysCompanyService {
    List<CompanyTreeVO> tree();
    SysCompany getById(Long id);
    void create(SysCompany company);
    void update(SysCompany company);
    void delete(Long id);
}
