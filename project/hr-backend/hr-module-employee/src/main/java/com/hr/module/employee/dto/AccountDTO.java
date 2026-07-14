package com.hr.module.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AccountDTO {
    @NotNull(message = "员工ID不能为空")
    private Long employeeId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private List<Long> roleIds;
}
