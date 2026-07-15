package com.hr.module.attendance.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * OA流程 Excel 导入模型
 */
@Data
public class AttOaFlowImportDTO {

    @ExcelProperty("员工ID")
    private Long employeeId;

    @ExcelProperty("类型")
    private String type;

    @ExcelProperty("开始日期")
    private String startDate;

    @ExcelProperty("结束日期")
    private String endDate;

    @ExcelProperty("时长")
    private String duration;

    @ExcelProperty("状态")
    private String status;
}
