package com.hr.module.employee.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransferVO {
    private Long id;
    private Long employeeId;
    private String empNo;
    private String employeeName;
    private String transferType;
    private String transferTypeName;
    private Long beforeDeptId;
    private String beforeDeptName;
    private Long afterDeptId;
    private String afterDeptName;
    private Long beforePositionId;
    private String beforePositionName;
    private Long afterPositionId;
    private String afterPositionName;
    private LocalDate effectiveDate;
    private String reason;
    private Integer status;
    private LocalDateTime createTime;
}
