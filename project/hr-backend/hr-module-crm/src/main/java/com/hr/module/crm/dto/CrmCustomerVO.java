package com.hr.module.crm.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CrmCustomerVO {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String industry;
    private String source;
    private String level;
    private String status;
    private Long ownerId;
    private String ownerName;
    private String province;
    private String city;
    private String address;
    private String contactName;
    private String contactPhone;
    private String remark;
    private LocalDateTime createTime;
}
