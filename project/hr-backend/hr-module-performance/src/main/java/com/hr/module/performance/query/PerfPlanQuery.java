package com.hr.module.performance.query;

import lombok.Data;

@Data
public class PerfPlanQuery {
    private String keyword;
    private Integer status;
    private Integer page = 1;
    private Integer pageSize = 10;
}
