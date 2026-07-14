package com.hr.module.salary.service;

import com.hr.module.salary.dto.SalFieldDTO;
import com.hr.module.salary.dto.SalFieldVO;

import java.util.List;

public interface SalFieldService {
    List<SalFieldVO> list();
    SalFieldVO getById(Long id);
    void create(SalFieldDTO dto);
    void update(Long id, SalFieldDTO dto);
    void delete(Long id);
}
