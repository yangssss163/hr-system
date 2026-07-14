package com.hr.module.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.employee.dto.EmployeeDTO;
import com.hr.module.employee.dto.EmployeeQuery;
import com.hr.module.employee.dto.EmployeeVO;

import java.util.List;

public interface HrEmployeeService {
    IPage<EmployeeVO> page(EmployeeQuery query);
    EmployeeVO getById(Long id);
    void create(EmployeeDTO dto);
    void update(Long id, EmployeeDTO dto);
    void delete(Long id);
    void batchDelete(List<Long> ids);
}
