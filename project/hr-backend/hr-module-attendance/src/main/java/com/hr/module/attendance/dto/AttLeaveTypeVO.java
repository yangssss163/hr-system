package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttLeaveTypeVO {
    private Long id;
    private String name;
    private String code;
    private java.math.BigDecimal defaultDays;
    private Integer enabled;
}
