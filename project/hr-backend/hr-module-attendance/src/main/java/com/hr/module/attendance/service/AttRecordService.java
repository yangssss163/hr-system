package com.hr.module.attendance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.attendance.dto.AttRecordBatchFixDTO;
import com.hr.module.attendance.dto.AttRecordQuery;
import com.hr.module.attendance.dto.AttRecordVO;

public interface AttRecordService {
    IPage<AttRecordVO> page(AttRecordQuery query);
    void batchFix(AttRecordBatchFixDTO dto);
}
