package com.hr.module.employee.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeVO {
    private Long id;
    private Long userId;
    private String empNo;
    private String name;
    private Integer gender;
    private String idCard;
    private LocalDate birthday;
    private String phone;
    private String email;
    private Long deptId;
    private String deptName;
    private Long positionId;
    private String positionName;
    private Long companyId;
    private String companyName;
    private LocalDate entryDate;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
