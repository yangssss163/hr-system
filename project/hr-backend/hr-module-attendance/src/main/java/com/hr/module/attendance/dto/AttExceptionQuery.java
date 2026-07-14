package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttExceptionQuery {
    private Long deptId;
    private String dateStart;
    private String dateEnd;
    private Integer page = 1;
    private Integer pageSize = 10;
}
