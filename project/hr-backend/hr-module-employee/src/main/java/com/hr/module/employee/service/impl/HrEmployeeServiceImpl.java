package com.hr.module.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.employee.dto.EmployeeDTO;
import com.hr.module.employee.dto.EmployeeImportDTO;
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
import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrEmployeeServiceImpl implements HrEmployeeService {

    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysPositionMapper sysPositionMapper;
    private final SysCompanyMapper sysCompanyMapper;

    @Override
    public IPage<EmployeeVO> page(EmployeeQuery query) {
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
        wrapper.orderByDesc(HrEmployee::getCreateTime);

        Page<HrEmployee> page = new Page<>(query.getPage(), query.getPageSize());
        Page<HrEmployee> empPage = hrEmployeeMapper.selectPage(page, wrapper);

        // 批量查询关联名称
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

    @Override
    @Transactional
    public void importExcel(MultipartFile file) throws IOException {
        List<Object> rawList = EasyExcel.read(file.getInputStream())
                .head(EmployeeImportDTO.class).sheet().doReadSync();
        for (Object obj : rawList) {
            EmployeeImportDTO dto = (EmployeeImportDTO) obj;
            HrEmployee e = new HrEmployee();
            e.setEmpNo(dto.getEmpNo());
            e.setName(dto.getName());
            e.setGender(dto.getGender());
            e.setIdCard(dto.getIdCard());
            if (dto.getBirthday() != null && !dto.getBirthday().isEmpty()) {
                e.setBirthday(LocalDate.parse(dto.getBirthday()));
            }
            e.setPhone(dto.getPhone());
            e.setEmail(dto.getEmail());
            e.setDeptId(dto.getDeptId());
            e.setPositionId(dto.getPositionId());
            e.setCompanyId(dto.getCompanyId());
            if (dto.getEntryDate() != null && !dto.getEntryDate().isEmpty()) {
                e.setEntryDate(LocalDate.parse(dto.getEntryDate()));
            }
            e.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            hrEmployeeMapper.insert(e);
        }
    }

    @Override
    public void exportExcel(HttpServletResponse response, EmployeeQuery query) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("员工花名册", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        LambdaQueryWrapper<HrEmployee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(HrEmployee::getName, query.getKeyword())
                    .or().like(HrEmployee::getEmpNo, query.getKeyword()));
        }
        if (query.getDeptId() != null) {
            wrapper.eq(HrEmployee::getDeptId, query.getDeptId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(HrEmployee::getStatus, query.getStatus());
        }
        List<HrEmployee> list = hrEmployeeMapper.selectList(wrapper);

        List<EmployeeImportDTO> exportList = new ArrayList<>();
        for (HrEmployee e : list) {
            EmployeeImportDTO ed = new EmployeeImportDTO();
            ed.setEmpNo(e.getEmpNo());
            ed.setName(e.getName());
            ed.setGender(e.getGender());
            ed.setIdCard(e.getIdCard());
            ed.setBirthday(e.getBirthday() != null ? e.getBirthday().toString() : null);
            ed.setPhone(e.getPhone());
            ed.setEmail(e.getEmail());
            ed.setDeptId(e.getDeptId());
            ed.setPositionId(e.getPositionId());
            ed.setCompanyId(e.getCompanyId());
            ed.setEntryDate(e.getEntryDate() != null ? e.getEntryDate().toString() : null);
            ed.setStatus(e.getStatus());
            exportList.add(ed);
        }
        EasyExcel.write(response.getOutputStream(), EmployeeImportDTO.class)
                .sheet("员工花名册").doWrite(exportList);
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

        // 填充关联名称
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
}
