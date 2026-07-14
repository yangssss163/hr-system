package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttShiftVO {
    private Long id;
    private String name;
    private String startTime;
    private String endTime;
    private Integer lateBuffer;
    private Integer earlyBuffer;
    private Integer status;
}
