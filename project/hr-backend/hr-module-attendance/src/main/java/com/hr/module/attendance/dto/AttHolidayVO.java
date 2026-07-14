package com.hr.module.attendance.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttHolidayVO {
    private Integer year;
    private List<AttHolidayItemVO> items;
}
