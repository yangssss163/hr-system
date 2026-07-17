package com.hr.module.salary.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalSheetEditDTO {
    @DecimalMin("0")
    private BigDecimal perfSalary;

    @DecimalMin("0")
    private BigDecimal subsidy;

    @DecimalMin("0")
    private BigDecimal overtimePay;
}
