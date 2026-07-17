package com.hr.module.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EmployeeImportResponse {
    private int successCount;
    private int failCount;
    private List<FailDetail> failDetails = new ArrayList<>();

    @Data
    @AllArgsConstructor
    public static class FailDetail {
        private int row;
        private String reason;
    }

    public void addSuccess() {
        this.successCount++;
    }

    public void addFail(int row, String reason) {
        this.failCount++;
        this.failDetails.add(new FailDetail(row, reason));
    }
}
