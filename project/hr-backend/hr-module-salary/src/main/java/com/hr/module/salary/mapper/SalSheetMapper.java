package com.hr.module.salary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.module.salary.entity.SalSheet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SalSheetMapper extends BaseMapper<SalSheet> {

    /**
     * 原生 SQL INSERT，完全绕过 MyBatis Plus @TableLogic 处理。
     * 实现：XML mapper SalSheetMapper.xml
     */
    int insertRaw(SalSheet sheet);

    /**
     * 原生 SQL UPDATE（按 employeeId+month），完全绕过 MyBatis Plus @TableLogic 处理。
     * 实现：XML mapper SalSheetMapper.xml
     */
    int updateRawByEmpMonth(SalSheet sheet);
}
