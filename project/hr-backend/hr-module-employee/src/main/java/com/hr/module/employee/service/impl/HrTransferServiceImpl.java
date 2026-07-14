package com.hr.module.employee.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.employee.dto.TransferDTO;
import com.hr.module.employee.dto.TransferQuery;
import com.hr.module.employee.dto.TransferVO;
import com.hr.module.employee.entity.HrEmployee;
import com.hr.module.employee.entity.HrTransfer;
import com.hr.module.employee.mapper.HrEmployeeMapper;
import com.hr.module.employee.mapper.HrTransferMapper;
import com.hr.module.system.entity.SysDept;
import com.hr.module.system.entity.SysPosition;
import com.hr.module.system.mapper.SysDeptMapper;
import com.hr.module.system.mapper.SysPositionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrTransferServiceImpl implements com.hr.module.employee.service.HrTransferService {

    private final HrTransferMapper hrTransferMapper;
    private final HrEmployeeMapper hrEmployeeMapper;
    private final SysDeptMapper sysDeptMapper;
    private final SysPositionMapper sysPositionMapper;

    private static final Map<String, String> TRANSFER_TYPE_MAP = Map.of(
            "regular", "转正",
            "transfer", "调岗",
            "promote", "晋升",
            "dimission", "离职",
            "rehire", "返聘"
    );

    @Override
    public IPage<TransferVO> page(TransferQuery query) {
        LambdaQueryWrapper<HrTransfer> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(HrTransfer::getEmployeeId, query.getEmployeeId());
        }
        if (StringUtils.hasText(query.getTransferType())) {
            wrapper.eq(HrTransfer::getTransferType, query.getTransferType());
        }
        if (StringUtils.hasText(query.getStartDate())) {
            wrapper.ge(HrTransfer::getEffectiveDate, LocalDate.parse(query.getStartDate()));
        }
        if (StringUtils.hasText(query.getEndDate())) {
            wrapper.le(HrTransfer::getEffectiveDate, LocalDate.parse(query.getEndDate()));
        }
        wrapper.orderByDesc(HrTransfer::getCreateTime);

        Page<HrTransfer> page = new Page<>(query.getPage(), query.getPageSize());
        Page<HrTransfer> transferPage = hrTransferMapper.selectPage(page, wrapper);

        // 批量查询员工信息
        List<Long> employeeIds = transferPage.getRecords().stream()
                .map(HrTransfer::getEmployeeId).distinct().collect(Collectors.toList());
        Map<Long, HrEmployee> empMap = employeeIds.isEmpty() ? Map.of() :
                hrEmployeeMapper.selectBatchIds(employeeIds).stream()
                        .collect(Collectors.toMap(HrEmployee::getId, e -> e));

        // 批量查询部门、职位名称
        List<Long> allDeptIds = transferPage.getRecords().stream()
                .flatMap(t -> {
                    java.util.Set<Long> ids = new java.util.HashSet<>();
                    if (t.getBeforeDeptId() != null) ids.add(t.getBeforeDeptId());
                    if (t.getAfterDeptId() != null) ids.add(t.getAfterDeptId());
                    return ids.stream();
                }).collect(Collectors.toList());
        List<Long> allPosIds = transferPage.getRecords().stream()
                .flatMap(t -> {
                    java.util.Set<Long> ids = new java.util.HashSet<>();
                    if (t.getBeforePositionId() != null) ids.add(t.getBeforePositionId());
                    if (t.getAfterPositionId() != null) ids.add(t.getAfterPositionId());
                    return ids.stream();
                }).collect(Collectors.toList());

        Map<Long, String> deptNameMap = allDeptIds.isEmpty() ? Map.of() :
                sysDeptMapper.selectBatchIds(allDeptIds).stream()
                        .collect(Collectors.toMap(SysDept::getId, SysDept::getName));
        Map<Long, String> posNameMap = allPosIds.isEmpty() ? Map.of() :
                sysPositionMapper.selectBatchIds(allPosIds).stream()
                        .collect(Collectors.toMap(SysPosition::getId, SysPosition::getName));

        List<TransferVO> voList = transferPage.getRecords().stream().map(t -> {
            TransferVO vo = new TransferVO();
            vo.setId(t.getId());
            vo.setEmployeeId(t.getEmployeeId());
            HrEmployee emp = empMap.get(t.getEmployeeId());
            if (emp != null) {
                vo.setEmpNo(emp.getEmpNo());
                vo.setEmployeeName(emp.getName());
            }
            vo.setTransferType(t.getTransferType());
            vo.setTransferTypeName(TRANSFER_TYPE_MAP.getOrDefault(t.getTransferType(), t.getTransferType()));
            vo.setBeforeDeptId(t.getBeforeDeptId());
            vo.setBeforeDeptName(deptNameMap.get(t.getBeforeDeptId()));
            vo.setAfterDeptId(t.getAfterDeptId());
            vo.setAfterDeptName(deptNameMap.get(t.getAfterDeptId()));
            vo.setBeforePositionId(t.getBeforePositionId());
            vo.setBeforePositionName(posNameMap.get(t.getBeforePositionId()));
            vo.setAfterPositionId(t.getAfterPositionId());
            vo.setAfterPositionName(posNameMap.get(t.getAfterPositionId()));
            vo.setEffectiveDate(t.getEffectiveDate());
            vo.setReason(t.getReason());
            vo.setStatus(t.getStatus());
            vo.setCreateTime(t.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        Page<TransferVO> result = new Page<>(transferPage.getCurrent(), transferPage.getSize(), transferPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public TransferVO getById(Long id) {
        HrTransfer t = hrTransferMapper.selectById(id);
        if (t == null) return null;

        TransferVO vo = new TransferVO();
        vo.setId(t.getId());
        vo.setEmployeeId(t.getEmployeeId());
        HrEmployee emp = hrEmployeeMapper.selectById(t.getEmployeeId());
        if (emp != null) {
            vo.setEmpNo(emp.getEmpNo());
            vo.setEmployeeName(emp.getName());
        }
        vo.setTransferType(t.getTransferType());
        vo.setTransferTypeName(TRANSFER_TYPE_MAP.getOrDefault(t.getTransferType(), t.getTransferType()));
        vo.setBeforeDeptId(t.getBeforeDeptId());
        if (t.getBeforeDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(t.getBeforeDeptId());
            vo.setBeforeDeptName(dept != null ? dept.getName() : "");
        }
        vo.setAfterDeptId(t.getAfterDeptId());
        if (t.getAfterDeptId() != null) {
            SysDept dept = sysDeptMapper.selectById(t.getAfterDeptId());
            vo.setAfterDeptName(dept != null ? dept.getName() : "");
        }
        vo.setBeforePositionId(t.getBeforePositionId());
        if (t.getBeforePositionId() != null) {
            SysPosition pos = sysPositionMapper.selectById(t.getBeforePositionId());
            vo.setBeforePositionName(pos != null ? pos.getName() : "");
        }
        vo.setAfterPositionId(t.getAfterPositionId());
        if (t.getAfterPositionId() != null) {
            SysPosition pos = sysPositionMapper.selectById(t.getAfterPositionId());
            vo.setAfterPositionName(pos != null ? pos.getName() : "");
        }
        vo.setEffectiveDate(t.getEffectiveDate());
        vo.setReason(t.getReason());
        vo.setStatus(t.getStatus());
        vo.setCreateTime(t.getCreateTime());
        return vo;
    }

    @Override
    @Transactional
    public void create(TransferDTO dto) {
        HrEmployee employee = hrEmployeeMapper.selectById(dto.getEmployeeId());
        if (employee == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "员工不存在");
        }

        HrTransfer transfer = new HrTransfer();
        transfer.setEmployeeId(dto.getEmployeeId());
        transfer.setTransferType(dto.getTransferType());
        transfer.setBeforeDeptId(employee.getDeptId());
        transfer.setBeforePositionId(employee.getPositionId());
        transfer.setAfterDeptId(dto.getAfterDeptId());
        transfer.setAfterPositionId(dto.getAfterPositionId());
        transfer.setEffectiveDate(LocalDate.parse(dto.getEffectiveDate()));
        transfer.setReason(dto.getReason());
        transfer.setStatus(1);
        hrTransferMapper.insert(transfer);

        // 更新员工信息
        if (dto.getAfterDeptId() != null) {
            employee.setDeptId(dto.getAfterDeptId());
        }
        if (dto.getAfterPositionId() != null) {
            employee.setPositionId(dto.getAfterPositionId());
        }
        // 离职时更新状态
        if ("dimission".equals(dto.getTransferType())) {
            employee.setStatus(2);
        }
        // 转正时更新状态
        if ("regular".equals(dto.getTransferType())) {
            employee.setStatus(1);
        }
        hrEmployeeMapper.updateById(employee);
    }

    @Override
    @Transactional
    public void revoke(Long id) {
        HrTransfer transfer = hrTransferMapper.selectById(id);
        if (transfer == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "异动记录不存在");
        }
        transfer.setStatus(0);
        hrTransferMapper.updateById(transfer);

        // 恢复员工原部门和职位
        HrEmployee employee = hrEmployeeMapper.selectById(transfer.getEmployeeId());
        if (employee != null) {
            if (transfer.getBeforeDeptId() != null) {
                employee.setDeptId(transfer.getBeforeDeptId());
            }
            if (transfer.getBeforePositionId() != null) {
                employee.setPositionId(transfer.getBeforePositionId());
            }
            employee.setStatus(1);
            hrEmployeeMapper.updateById(employee);
        }
    }
}
