package com.hr.module.employee.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BatchDeleteDTO {
    @NotNull(message = "ID列表不能为空")
    private List<Long> ids;
}
