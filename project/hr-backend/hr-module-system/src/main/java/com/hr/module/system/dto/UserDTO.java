package com.hr.module.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    @NotNull(message = "部门不能为空")
    private Long deptId;

    private String phone;
    private String email;
    private List<Long> roleIds;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
