package com.hr.module.recruitment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.module.recruitment.dto.BlacklistDTO;
import com.hr.module.recruitment.dto.BlacklistVO;

public interface RecBlacklistService {
    IPage<BlacklistVO> page(Integer page, Integer pageSize, String keyword);
    BlacklistVO getById(Long id);
    void create(BlacklistDTO dto);
    void delete(Long id);
}
