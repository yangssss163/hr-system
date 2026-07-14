package com.hr.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hr.module.system.entity.SysFieldConfig;
import com.hr.module.system.mapper.SysFieldConfigMapper;
import com.hr.module.system.service.SysFieldConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysFieldConfigServiceImpl implements SysFieldConfigService {

    private final SysFieldConfigMapper sysFieldConfigMapper;

    @Override
    public List<SysFieldConfig> list(String module) {
        LambdaQueryWrapper<SysFieldConfig> wrapper = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) {
            wrapper.eq(SysFieldConfig::getModule, module);
        }
        wrapper.orderByAsc(SysFieldConfig::getSort);
        return sysFieldConfigMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void batchSave(List<SysFieldConfig> configs) {
        for (SysFieldConfig config : configs) {
            if (config.getId() != null) {
                sysFieldConfigMapper.updateById(config);
            } else {
                sysFieldConfigMapper.insert(config);
            }
        }
    }
}
