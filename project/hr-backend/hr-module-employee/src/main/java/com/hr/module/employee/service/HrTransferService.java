package com.hr.module.employee.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.employee.dto.TransferDTO;
import com.hr.module.employee.dto.TransferQuery;
import com.hr.module.employee.dto.TransferVO;

public interface HrTransferService {
    IPage<TransferVO> page(TransferQuery query);
    TransferVO getById(Long id);
    void create(TransferDTO dto);
    void revoke(Long id);
}
