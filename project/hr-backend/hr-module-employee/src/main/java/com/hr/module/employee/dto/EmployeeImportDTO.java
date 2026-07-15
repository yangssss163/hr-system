package com.hr.module.employee.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 员工 Excel 导入/导出模型
 */
@Data
public class EmployeeImportDTO {

    @ExcelProperty("工号")
    private String empNo;

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private Integer gender;

    @ExcelProperty("身份证号")
    private String idCard;

    @ExcelProperty("生日")
    private String birthday;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("部门ID")
    private Long deptId;

    @ExcelProperty("职位ID")
    private Long positionId;

    @ExcelProperty("公司ID")
    private Long companyId;

    @ExcelProperty("入职日期")
    private String entryDate;

    @ExcelProperty("状态")
    private Integer status;
}
