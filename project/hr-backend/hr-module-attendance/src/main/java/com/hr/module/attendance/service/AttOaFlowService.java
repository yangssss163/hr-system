package com.hr.module.attendance.service;

import com.hr.common.result.PageResult;
import com.hr.module.attendance.dto.AttOaFlowQuery;
import com.hr.module.attendance.dto.AttOaFlowVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttOaFlowService {
    PageResult<AttOaFlowVO> page(AttOaFlowQuery query);
    AttOaFlowVO getById(Long id);
    void importFlows(MultipartFile file) throws IOException;
}
