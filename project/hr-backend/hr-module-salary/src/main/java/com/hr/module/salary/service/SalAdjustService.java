package com.hr.module.salary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.salary.dto.SalAdjustDTO;
import com.hr.module.salary.dto.SalAdjustQuery;
import com.hr.module.salary.dto.SalAdjustVO;

public interface SalAdjustService {
    IPage<SalAdjustVO> page(SalAdjustQuery query);
    void create(SalAdjustDTO dto);
}
