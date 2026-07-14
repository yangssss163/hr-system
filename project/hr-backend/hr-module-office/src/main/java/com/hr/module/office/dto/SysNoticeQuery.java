package com.hr.module.office.dto;

import lombok.Data;

@Data
public class SysNoticeQuery {
    private String keyword;
    private Integer type;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
