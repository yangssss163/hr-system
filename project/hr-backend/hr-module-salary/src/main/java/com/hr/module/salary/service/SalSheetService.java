package com.hr.module.salary.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.salary.dto.SalSheetGenerateDTO;
import com.hr.module.salary.dto.SalSheetQuery;
import com.hr.module.salary.dto.SalSheetSyncDTO;
import com.hr.module.salary.dto.SalSheetVO;

public interface SalSheetService {
    IPage<SalSheetVO> page(SalSheetQuery query);
    void sync(SalSheetSyncDTO dto);
    void generate(SalSheetGenerateDTO dto);
}
