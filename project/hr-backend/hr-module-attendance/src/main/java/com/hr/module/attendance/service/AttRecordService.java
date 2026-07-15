package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttRecordBatchFixDTO;
import com.hr.module.attendance.dto.AttRecordQuery;
import com.hr.module.attendance.dto.AttRecordVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttRecordService {
    PageResult<AttRecordVO> page(AttRecordQuery query);
    void batchFix(AttRecordBatchFixDTO dto);
    void importRecords(MultipartFile file) throws IOException;
}
