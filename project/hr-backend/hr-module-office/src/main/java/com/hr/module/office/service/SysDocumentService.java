package com.hr.module.office.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.office.dto.SysDocumentDTO;
import com.hr.module.office.dto.SysDocumentQuery;
import com.hr.module.office.dto.SysDocumentVO;

public interface SysDocumentService {
    IPage<SysDocumentVO> page(SysDocumentQuery query);
    SysDocumentVO getById(Long id);
    void create(SysDocumentDTO dto);
    void update(Long id, SysDocumentDTO dto);
    void delete(Long id);
}
