package com.hr.module.employee.service;

import com.hr.module.employee.dto.AccountDTO;

public interface HrAccountService {
    void openAccount(AccountDTO dto);
    void toggleAccount(Long id);
}
