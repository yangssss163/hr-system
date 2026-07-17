package com.hr.module.salary.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hr.common.result.Result;
import com.hr.common.utils.ExcelUtils;
import com.hr.module.salary.dto.*;
import com.hr.module.salary.service.SalSheetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "工资表管理")
@RestController
@RequestMapping("/api/salary-sheets")
@RequiredArgsConstructor
public class SheetController {

    private final SalSheetService salSheetService;

    @Operation(summary = "工资表列表")
    @GetMapping
    @PreAuthorize("hasAuthority('salary:sheet:list')")
    public Result<IPage<SalSheetVO>> list(@Valid SalSheetQuery query) {
        return Result.success(salSheetService.page(query));
    }

    @Operation(summary = "同步绩效考勤数据")
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('salary:sheet:sync')")
    public Result<Void> sync(@Valid @RequestBody SalSheetSyncDTO dto) {
        salSheetService.sync(dto);
        return Result.success();
    }

    @Operation(summary = "生成工资表")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('salary:sheet:generate')")
    public Result<Void> generate(@Valid @RequestBody SalSheetGenerateDTO dto) {
        salSheetService.generate(dto);
        return Result.success();
    }

    @Operation(summary = "编辑绩效/补贴/加班费")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('salary:sheet:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestBody SalSheetEditDTO dto) {
        salSheetService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "清除工资表数据（按状态）")
    @DeleteMapping("/clear")
    public Result<Integer> clear(@RequestParam(defaultValue = "0") Integer status) {
        return Result.success(salSheetService.deleteByStatus(status));
    }

    @Operation(summary = "批量导入绩效/补贴/加班费")
    @PostMapping("/import")
    @PreAuthorize("hasAuthority('salary:sheet:import')")
    public Result<Integer> importData(@RequestParam("file") MultipartFile file) {
        return Result.success(salSheetService.importExcel(file));
    }

    @Operation(summary = "导出工资表（仅导出已生成=1 的记录，导出后标记为已导出=2）")
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('salary:sheet:export')")
    public void export(HttpServletResponse response, SalSheetQuery query) throws Exception {
        // 只导出 status=1（已生成）的记录
        String month = query.getMonth();
        query.setPage(1);
        query.setPageSize(Integer.MAX_VALUE);
        IPage<SalSheetVO> page = salSheetService.page(query);

        List<SalSheetExcelVO> list = page.getRecords().stream()
                .filter(vo -> vo.getStatus() != null && vo.getStatus() == 1)
                .map(vo -> {
                    SalSheetExcelVO excel = new SalSheetExcelVO();
                    excel.setMonth(vo.getMonth());
                    excel.setEmpNo(vo.getEmpNo());
                    excel.setEmployeeName(vo.getEmployeeName());
                    excel.setDeptName(vo.getDeptName());
                    excel.setBaseSalary(vo.getBaseSalary());
                    excel.setPerfSalary(vo.getPerfSalary());
                    excel.setSubsidy(vo.getSubsidy());
                    excel.setOvertimePay(vo.getOvertimePay());
                    excel.setTotalIncome(vo.getTotalIncome());
                    excel.setSocialInsurance(vo.getSocialInsurance());
                    excel.setTax(vo.getTax());
                    excel.setTotalDeduction(vo.getTotalDeduction());
                    excel.setNetSalary(vo.getNetSalary());
                    return excel;
                }).collect(Collectors.toList());

        if (list.isEmpty()) {
            response.setStatus(404);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":404,\"message\":\"没有可导出的已生成数据，请先生成工资表\"}");
            return;
        }

        // 标记为已导出
        int marked = salSheetService.markExported(month);
        log.info("标记已导出: month={}, {} 条", month, marked);

        ExcelUtils.export(response, "工资表_" + (month != null ? month : "全部"),
                "工资表", SalSheetExcelVO.class, list);
    }
}
