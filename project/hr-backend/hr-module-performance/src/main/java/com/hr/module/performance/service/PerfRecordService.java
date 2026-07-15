package com.hr.module.performance.service;

import com.hr.common.result.PageResult;
import com.hr.module.performance.dto.PerfRecordDTO;
import com.hr.module.performance.query.PerfRecordQuery;
import com.hr.module.performance.vo.PerfRecordDetailVO;
import com.hr.module.performance.vo.PerfRecordVO;

public interface PerfRecordService {
    PageResult<PerfRecordVO> page(PerfRecordQuery query);
    PerfRecordDetailVO getById(Long id);
    void create(PerfRecordDTO dto);
    void update(Long id, PerfRecordDTO dto);
    void delete(Long id);
}
