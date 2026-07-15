package com.hr.module.attendance.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 打卡记录 Excel 导入模型
 */
@Data
public class AttRecordImportDTO {

    @ExcelProperty("员工ID")
    private Long employeeId;

    @ExcelProperty("考勤日期")
    private String recordDate;

    @ExcelProperty("签到时间")
    private String checkIn;

    @ExcelProperty("签退时间")
    private String checkOut;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("来源")
    private String source;
}
