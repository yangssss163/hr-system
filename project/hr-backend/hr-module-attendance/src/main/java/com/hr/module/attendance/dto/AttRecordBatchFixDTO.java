package com.hr.module.attendance.dto;

import lombok.Data;

import java.util.List;

@Data
public class AttRecordBatchFixDTO {
    private List<Long> ids;
    private String checkIn;
    private String checkOut;
    private String remark;
}
