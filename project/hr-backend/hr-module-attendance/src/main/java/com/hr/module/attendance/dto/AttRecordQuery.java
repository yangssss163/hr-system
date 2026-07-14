package com.hr.module.attendance.dto;

import lombok.Data;

@Data
public class AttRecordQuery {
    private Long employeeId;
    private Long deptId;
    private String dateStart;
    private String dateEnd;
    private String status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
