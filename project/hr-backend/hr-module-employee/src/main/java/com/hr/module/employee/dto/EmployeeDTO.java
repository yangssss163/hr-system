package com.hr.module.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeDTO {
    @NotBlank(message = "工号不能为空")
    private String empNo;

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotNull(message = "性别不能为空")
    private Integer gender;

    private String idCard;
    private String birthday;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String email;

    @NotNull(message = "部门不能为空")
    private Long deptId;

    @NotNull(message = "职位不能为空")
    private Long positionId;

    @NotNull(message = "公司不能为空")
    private Long companyId;

    @NotBlank(message = "入职日期不能为空")
    private String entryDate;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
