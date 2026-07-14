package com.hr.module.attendance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AttHolidayItemVO {
    private Long id;
    private String name;
    private LocalDate date;
    private Integer days;
}
