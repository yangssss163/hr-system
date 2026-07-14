package com.hr.module.performance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("perf_record_item")
public class PerfRecordItem implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private String indicator;
    private Integer weight;
    private BigDecimal score;
}
