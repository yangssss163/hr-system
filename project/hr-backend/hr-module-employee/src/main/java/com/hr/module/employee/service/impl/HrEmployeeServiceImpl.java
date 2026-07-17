package com.hr.module.employee.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.common.utils.ExcelUtils;
import com.hr.module.employee.dto.EmployeeDTO;
import com.hr.module.employee.dto.EmployeeExcelVO;
import com.hr.module.employee.dto.EmployeeImportResponse;
import com.hr.module.employee.dto.EmployeeQuery;
import com.hr.module.employee.dto.EmployeeVO;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.employee.service.HrEmployeeService;
import com.hr.module.system.entity.SysCompany;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.entity.SysPosition;
import com.hr.module.system.mapper.SysCompanyMapper;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.mapper.SysPositionMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HrEmployeeServiceImpl implements HrEmployeeService {

    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysPositionMapper sysPositionMapper;
    private final SysCompanyMapper sysCompanyMapper;

    @Override
    public IPage<EmployeeVO> page(EmployeeQuery query) {
        LambdaQueryWrapper<HrEmployee> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(HrEmployee::getCreateTime);

        Page<HrEmployee> page = new Page<>(query.getPage(), query.getPageSize());
        Page<HrEmployee> empPage = hrEmployeeMapper.selectPage(page, wrapper);

        List<Long> deptIds = empPage.getRecords().stream()
                .map(HrEmployee::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> positionIds = empPage.getRecords().stream()
                .map(HrEmployee::getPositionId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> companyIds = empPage.getRecords().stream()
                .map(HrEmployee::getCompanyId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));
        Map<Long, String> posNameMap = positionIds.isEmpty() ? Map.of() :
                sysPositionMapper.selectBatchIds(positionIds).stream()
                        .collect(Collectors.toMap(SysPosition::getId, SysPosition::getName));
        Map<Long, String> companyNameMap = companyIds.isEmpty() ? Map.of() :
                sysCompanyMapper.selectBatchIds(companyIds).stream()
                        .collect(Collectors.toMap(SysCompany::getId, SysCompany::getName));

        List<EmployeeVO> voList = empPage.getRecords().stream().map(e -> toVO(e, deptNameMap, posNameMap, companyNameMap))
                .collect(Collectors.toList());

        Page<EmployeeVO> result = new Page<>(empPage.getCurrent(), empPage.getSize(), empPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public EmployeeVO getById(Long id) {
        HrEmployee e = hrEmployeeMapper.selectById(id);
        if (e == null) return null;
        return toVO(e, null, null, null);
    }

    @Override
    @Transactional
    public void create(EmployeeDTO dto) {
        Long count = hrEmployeeMapper.selectCount(
                new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getEmpNo, dto.getEmpNo()));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "工号已存在");
        }
        HrEmployee e = new HrEmployee();
        applyDTO(e, dto);
        hrEmployeeMapper.insert(e);
    }

    @Override
    @Transactional
    public void update(Long id, EmployeeDTO dto) {
        HrEmployee e = hrEmployeeMapper.selectById(id);
        if (e == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "员工不存在");
        }
        Long count = hrEmployeeMapper.selectCount(
                new LambdaQueryWrapper<HrEmployee>()
                        .eq(HrEmployee::getEmpNo, dto.getEmpNo())
                        .ne(HrEmployee::getId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_RECORD.getCode(), "工号已存在");
        }
        applyDTO(e, dto);
        hrEmployeeMapper.updateById(e);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        hrEmployeeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        hrEmployeeMapper.deleteBatchIds(ids);
    }

    // ==================== 导入导出 ====================

    @Override
    @Transactional
    public EmployeeImportResponse importExcel(MultipartFile file) {
        EmployeeImportResponse result = new EmployeeImportResponse();
        try {
            EasyExcel.read(file.getInputStream(), EmployeeImportRow.class,
                    new EmployeeImportListener(result, this)).sheet().doRead();
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new BusinessException(ResultCode.IMPORT_FILE_FORMAT_ERROR.getCode(), "无法读取导入文件");
        }
        return result;
    }

    @Override
    public void exportExcel(HttpServletResponse response, EmployeeQuery query) {
        LambdaQueryWrapper<HrEmployee> wrapper = buildQueryWrapper(query);
        wrapper.orderByDesc(HrEmployee::getCreateTime);
        List<HrEmployee> list = hrEmployeeMapper.selectList(wrapper);

        List<Long> deptIds = list.stream()
                .map(HrEmployee::getDeptId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> positionIds = list.stream()
                .map(HrEmployee::getPositionId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Long> companyIds = list.stream()
                .map(HrEmployee::getCompanyId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, String> deptNameMap = deptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(deptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));
        Map<Long, String> posNameMap = positionIds.isEmpty() ? Map.of() :
                sysPositionMapper.selectBatchIds(positionIds).stream()
                        .collect(Collectors.toMap(SysPosition::getId, SysPosition::getName));
        Map<Long, String> companyNameMap = companyIds.isEmpty() ? Map.of() :
                sysCompanyMapper.selectBatchIds(companyIds).stream()
                        .collect(Collectors.toMap(SysCompany::getId, SysCompany::getName));

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<EmployeeExcelVO> excelList = list.stream().map(e -> {
            EmployeeExcelVO vo = new EmployeeExcelVO();
            vo.setEmpNo(e.getEmpNo());
            vo.setName(e.getName());
            vo.setGenderName(e.getGender() != null && e.getGender() == 1 ? "男" : "女");
            vo.setPhone(e.getPhone());
            vo.setEmail(e.getEmail());
            vo.setIdCard(e.getIdCard());
            vo.setBirthday(e.getBirthday() != null ? e.getBirthday().format(dateFmt) : "");
            vo.setCompanyName(companyNameMap.getOrDefault(e.getCompanyId(), ""));
            vo.setDeptName(deptNameMap.getOrDefault(e.getDeptId(), ""));
            vo.setPositionName(posNameMap.getOrDefault(e.getPositionId(), ""));
            vo.setEntryDate(e.getEntryDate() != null ? e.getEntryDate().format(dateFmt) : "");
            vo.setStatusName(getStatusName(e.getStatus()));
            vo.setCreateTime(e.getCreateTime() != null ? e.getCreateTime().format(timeFmt) : "");
            return vo;
        }).collect(Collectors.toList());

        ExcelUtils.export(response, "员工花名册", "员工信息", EmployeeExcelVO.class, excelList);
    }

    // ==================== 私有方法 ====================

    private LambdaQueryWrapper<HrEmployee> buildQueryWrapper(EmployeeQuery query) {
        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(HrEmployee::getName, query.getKeyword())
                    .or().like(HrEmployee::getEmpNo, query.getKeyword()));
        }
        if (query.getDeptId() != null) {
            wrapper.eq(HrEmployee::getDeptId, query.getDeptId());
        }
        if (query.getPositionId() != null) {
            wrapper.eq(HrEmployee::getPositionId, query.getPositionId());
        }
        if (query.getCompanyId() != null) {
            wrapper.eq(HrEmployee::getCompanyId, query.getCompanyId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(HrEmployee::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getEntryDateStart())) {
            wrapper.ge(HrEmployee::getEntryDate, LocalDate.parse(query.getEntryDateStart()));
        }
        if (StringUtils.hasText(query.getEntryDateEnd())) {
            wrapper.le(HrEmployee::getEntryDate, LocalDate.parse(query.getEntryDateEnd()));
        }
        return wrapper;
    }

    private void applyDTO(HrEmployee e, EmployeeDTO dto) {
        e.setEmpNo(dto.getEmpNo());
        e.setName(dto.getName());
        e.setGender(dto.getGender());
        e.setIdCard(dto.getIdCard());
        if (StringUtils.hasText(dto.getBirthday())) {
            e.setBirthday(LocalDate.parse(dto.getBirthday()));
        }
        e.setPhone(dto.getPhone());
        e.setEmail(dto.getEmail());
        e.setDeptId(dto.getDeptId());
        e.setPositionId(dto.getPositionId());
        e.setCompanyId(dto.getCompanyId());
        e.setEntryDate(LocalDate.parse(dto.getEntryDate()));
        e.setStatus(dto.getStatus());
    }

    private EmployeeVO toVO(HrEmployee e, Map<Long, String> deptNameMap, Map<Long, String> posNameMap, Map<Long, String> companyNameMap) {
        EmployeeVO vo = new EmployeeVO();
        vo.setId(e.getId());
        vo.setUserId(e.getUserId());
        vo.setEmpNo(e.getEmpNo());
        vo.setName(e.getName());
        vo.setGender(e.getGender());
        vo.setIdCard(e.getIdCard());
        vo.setBirthday(e.getBirthday());
        vo.setPhone(e.getPhone());
        vo.setEmail(e.getEmail());
        vo.setDeptId(e.getDeptId());
        vo.setPositionId(e.getPositionId());
        vo.setCompanyId(e.getCompanyId());
        vo.setEntryDate(e.getEntryDate());
        vo.setStatus(e.getStatus());
        vo.setCreateTime(e.getCreateTime());
        vo.setUpdateTime(e.getUpdateTime());

        if (e.getDeptId() != null) {
            vo.setDeptName(deptNameMap != null ? deptNameMap.get(e.getDeptId()) : getDeptName(e.getDeptId()));
        }
        if (e.getPositionId() != null) {
            vo.setPositionName(posNameMap != null ? posNameMap.get(e.getPositionId()) : getPositionName(e.getPositionId()));
        }
        if (e.getCompanyId() != null) {
            vo.setCompanyName(companyNameMap != null ? companyNameMap.get(e.getCompanyId()) : getCompanyName(e.getCompanyId()));
        }
        return vo;
    }

    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "在职";
            case 2: return "离职";
            case 3: return "试用";
            default: return "未知";
        }
    }

    private String getDeptName(Long deptId) {
        SysDept dept = sysDeptMapper.selectById(deptId);
        return dept != null ? dept.getName() : "";
    }

    private String getPositionName(Long positionId) {
        SysPosition pos = sysPositionMapper.selectById(positionId);
        return pos != null ? pos.getName() : "";
    }

    private String getCompanyName(Long companyId) {
        SysCompany company = sysCompanyMapper.selectById(companyId);
        return company != null ? company.getName() : "";
    }

    // ==================== Excel 导入行定义 + 监听器 ====================

    @Data
    public static class EmployeeImportRow {
        @ExcelProperty("工号")
        private String empNo;
        @ExcelProperty("姓名")
        private String name;
        @ExcelProperty("性别")
        private String gender;
        @ExcelProperty("手机号")
        private String phone;
        @ExcelProperty("邮箱")
        private String email;
        @ExcelProperty("身份证号")
        private String idCard;
        @ExcelProperty("出生日期")
        private String birthday;
        @ExcelProperty("所属公司ID")
        private Long companyId;
        @ExcelProperty("所属部门ID")
        private Long deptId;
        @ExcelProperty("职位ID")
        private Long positionId;
        @ExcelProperty("入职日期")
        private String entryDate;
        @ExcelProperty("状态")
        private String status;
    }

    @Slf4j
    @RequiredArgsConstructor
    public static class EmployeeImportListener extends AnalysisEventListener<EmployeeImportRow> {

        private final EmployeeImportResponse result;
        private final HrEmployeeServiceImpl service;

        @Override
        public void invoke(EmployeeImportRow row, AnalysisContext context) {
            Integer rowIndex = context.readRowHolder().getRowIndex() + 1;
            try {
                // 校验必填字段
                if (!StringUtils.hasText(row.getEmpNo())) {
                    result.addFail(rowIndex, "工号不能为空");
                    return;
                }
                if (!StringUtils.hasText(row.getName())) {
                    result.addFail(rowIndex, "姓名不能为空");
                    return;
                }
                if (!StringUtils.hasText(row.getPhone())) {
                    result.addFail(rowIndex, "手机号不能为空");
                    return;
                }
                if (row.getCompanyId() == null) {
                    result.addFail(rowIndex, "所属公司ID不能为空");
                    return;
                }
                if (row.getDeptId() == null) {
                    result.addFail(rowIndex, "所属部门ID不能为空");
                    return;
                }
                if (row.getPositionId() == null) {
                    result.addFail(rowIndex, "职位ID不能为空");
                    return;
                }
                if (!StringUtils.hasText(row.getEntryDate())) {
                    result.addFail(rowIndex, "入职日期不能为空");
                    return;
                }

                // 解析性别
                Integer gender = parseGender(row.getGender());
                if (gender == null) {
                    result.addFail(rowIndex, "性别格式错误，请填写「男」或「女」");
                    return;
                }

                // 解析状态
                Integer status = parseStatus(row.getStatus());
                if (status == null) {
                    status = 1; // 默认在职
                }

                // 校验工号唯一性
                Long count = Long.valueOf(service.hrEmployeeMapper.selectCount(
                        new LambdaQueryWrapper<HrEmployee>().eq(HrEmployee::getEmpNo, row.getEmpNo())));
                if (count > 0) {
                    result.addFail(rowIndex, "工号「" + row.getEmpNo() + "」已存在");
                    return;
                }

                // 构建并插入
                EmployeeDTO dto = new EmployeeDTO();
                dto.setEmpNo(row.getEmpNo());
                dto.setName(row.getName());
                dto.setGender(gender);
                dto.setPhone(row.getPhone());
                dto.setEmail(row.getEmail());
                dto.setIdCard(row.getIdCard());
                dto.setBirthday(row.getBirthday());
                dto.setCompanyId(row.getCompanyId());
                dto.setDeptId(row.getDeptId());
                dto.setPositionId(row.getPositionId());
                dto.setEntryDate(row.getEntryDate());
                dto.setStatus(status);

                service.create(dto);
                result.addSuccess();
            } catch (Exception e) {
                log.error("导入第 {} 行失败: {}", rowIndex, e.getMessage());
                result.addFail(rowIndex, e.getMessage() != null ? e.getMessage() : "未知错误");
            }
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            log.info("导入完成: 成功 {} 条, 失败 {} 条", result.getSuccessCount(), result.getFailCount());
        }

        private Integer parseGender(String val) {
            if (!StringUtils.hasText(val)) return 1;
            if (val.contains("男") || "1".equals(val.trim())) return 1;
            if (val.contains("女") || "2".equals(val.trim())) return 2;
            return null;
        }

        private Integer parseStatus(String val) {
            if (!StringUtils.hasText(val)) return null;
            if (val.contains("在职") || "1".equals(val.trim())) return 1;
            if (val.contains("离职") || "2".equals(val.trim())) return 2;
            if (val.contains("试用") || "3".equals(val.trim())) return 3;
            return null;
        }
    }
}
