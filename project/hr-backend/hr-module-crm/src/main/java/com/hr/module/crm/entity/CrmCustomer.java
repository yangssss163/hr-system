package com.hr.module.crm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hr.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("crm_customer")
public class CrmCustomer extends BaseEntity {
    private String name;
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
