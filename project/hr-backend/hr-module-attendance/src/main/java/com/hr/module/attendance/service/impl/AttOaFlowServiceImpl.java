package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.attendance.dto.AttOaFlowQuery;
import com.hr.module.attendance.dto.AttOaFlowVO;
import com.hr.module.attendance.entity.AttOaFlow;
import com.hr.module.attendance.mapper.AttOaFlowMapper;
import com.hr.module.attendance.service.AttOaFlowService;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.common.result.PageResult;
import com.alibaba.excel.EasyExcel;
import com.hr.module.attendance.dto.AttOaFlowImportDTO;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper;
import com.hr.module.attendance.helper.AttendanceEmployeeHelper.EmployeeInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttOaFlowServiceImpl implements AttOaFlowService {

    private final AttOaFlowMapper attOaFlowMapper;
    private final AttendanceEmployeeHelper employeeHelper;

    @Override
    public PageResult<AttOaFlowVO> page(AttOaFlowQuery query) {
        LambdaQueryWrapper<AttOaFlow> wrapper = new LambdaQueryWrapper<>();
        if (query.getEmployeeId() != null) {
            wrapper.eq(AttOaFlow::getEmployeeId, query.getEmployeeId());
        }
        if (StringUtils.hasText(query.getType())) {
            wrapper.eq(AttOaFlow::getType, query.getType());
        }
        wrapper.orderByDesc(AttOaFlow::getCreateTime);

        Page<AttOaFlow> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttOaFlow> result = attOaFlowMapper.selectPage(page, wrapper);

        List<AttOaFlowVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        // 填充员工信息
        Set<Long> empIds = result.getRecords().stream().map(AttOaFlow::getEmployeeId).collect(Collectors.toSet());
        Map<Long, EmployeeInfo> empMap = employeeHelper.getEmployeeInfoMap(empIds);
        voList.forEach(vo -> {
            EmployeeInfo info = empMap.get(vo.getEmployeeId());
            if (info != null) {
                vo.setEmployeeName(info.getEmployeeName());
            }
        });
        PageResult<AttOaFlowVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPage((int) result.getCurrent());
        pageResult.setPageSize((int) result.getSize());
        pageResult.setRecords(voList);
        return pageResult;
    }

    @Override
    public AttOaFlowVO getById(Long id) {
        AttOaFlow entity = attOaFlowMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "OA流程不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void importFlows(MultipartFile file) throws IOException {
        List<Object> rawList = EasyExcel.read(file.getInputStream())
                .head(AttOaFlowImportDTO.class).sheet().doReadSync();
        for (Object obj : rawList) {
            AttOaFlowImportDTO dto = (AttOaFlowImportDTO) obj;
            // 校验员工是否存在
            if (!employeeHelper.employeeExists(dto.getEmployeeId())) {
                throw new BusinessException(ResultCode.BAD_REQUEST.getCode(),
                        "员工ID " + dto.getEmployeeId() + " 不存在，请检查导入数据");
            }
            AttOaFlow flow = new AttOaFlow();
            flow.setEmployeeId(dto.getEmployeeId());
            flow.setType(dto.getType());
            if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
                flow.setStartDate(LocalDate.parse(dto.getStartDate()));
            }
            if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
                flow.setEndDate(LocalDate.parse(dto.getEndDate()));
            }
            if (dto.getDuration() != null && !dto.getDuration().isEmpty()) {
                flow.setDuration(new BigDecimal(dto.getDuration()));
            }
            flow.setStatus(dto.getStatus());
            attOaFlowMapper.insert(flow);
        }
    }

    private AttOaFlowVO toVO(AttOaFlow entity) {
        AttOaFlowVO vo = new AttOaFlowVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setType(entity.getType());
        vo.setStartDate(entity.getStartDate());
        vo.setEndDate(entity.getEndDate());
        vo.setDuration(entity.getDuration());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
