package com.hr.module.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.module.employee.entity.HrEmployee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HrEmployeeMapper extends BaseMapper<HrEmployee> {
}
