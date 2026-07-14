package com.hr.module.system.service;

import com.hr.module.system.entity.SysFieldConfig;

import java.util.List;

public interface SysFieldConfigService {
    List<SysFieldConfig> list(String module);
    void batchSave(List<SysFieldConfig> configs);
}
