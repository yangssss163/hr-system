package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttCardRuleVO {
    private Long id;
    private String name;
    private Integer minCardCount;
    private Integer allowOvertime;
    private Integer status;
}
