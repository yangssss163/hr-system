package com.hr.module.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrmCustomerDTO {
    @NotBlank(message = "客户名称不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String email;
    private String industry;
    private String source;
    private String level;
    private String status;
    private Long ownerId;
    private String province;
    private String city;
    private String address;
    private String contactName;
    private String contactPhone;
    private String remark;
}
