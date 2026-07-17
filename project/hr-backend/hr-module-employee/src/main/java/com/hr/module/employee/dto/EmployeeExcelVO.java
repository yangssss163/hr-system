package com.hr.module.employee.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class EmployeeExcelVO {

    @ExcelProperty("工号")
    @ColumnWidth(15)
    private String empNo;

    @ExcelProperty("姓名")
    @ColumnWidth(12)
    private String name;

    @ExcelProperty("性别")
    @ColumnWidth(8)
    private String genderName;

    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;

    @ExcelProperty("身份证号")
    @ColumnWidth(22)
    private String idCard;

    @ExcelProperty("出生日期")
    @ColumnWidth(14)
    private String birthday;

    @ExcelProperty("所属公司")
    @ColumnWidth(20)
    private String companyName;

    @ExcelProperty("所属部门")
    @ColumnWidth(20)
    private String deptName;

    @ExcelProperty("职位")
    @ColumnWidth(18)
    private String positionName;

    @ExcelProperty("入职日期")
    @ColumnWidth(14)
    private String entryDate;

    @ExcelProperty("状态")
    @ColumnWidth(10)
    private String statusName;

    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private String createTime;

    @ExcelIgnore
    private Long deptId;

    @ExcelIgnore
    private Long positionId;

    @ExcelIgnore
    private Long companyId;
}
