package com.hr.module.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLeaveDTO;
import com.hr.module.workflow.dto.FlowLeaveQuery;
import com.hr.module.workflow.dto.FlowLeaveVO;
import com.hr.module.workflow.entity.FlowLeave;
import com.hr.module.workflow.mapper.FlowLeaveMapper;
import com.hr.module.workflow.service.FlowLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowLeaveServiceImpl implements FlowLeaveService {

    private static final Map<String, String> STATUS_NAME_MAP = Map.of(
            "pending", "待审批",
            "approved", "已批准",
            "rejected", "已拒绝"
    );

    private final FlowLeaveMapper flowLeaveMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<FlowLeaveVO> page(FlowLeaveQuery query) {
        LambdaQueryWrapper<FlowLeave> wrapper = new LambdaQueryWrapper<>();
        if (query.getApplicantId() != null) {
            wrapper.eq(FlowLeave::getApplicantId, query.getApplicantId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(FlowLeave::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(FlowLeave::getCreateTime);

        Page<FlowLeave> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<FlowLeave> result = flowLeaveMapper.selectPage(page, wrapper);

        // 批量查询用户名
        List<Long> userIds = result.getRecords().stream()
                .flatMap(e -> java.util.stream.Stream.of(e.getApplicantId(), e.getApproverId()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> userNameMap = userIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(userIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        return result.convert(e -> toVO(e, userNameMap));
    }

    @Override
    public FlowLeaveVO getById(Long id) {
        FlowLeave entity = flowLeaveMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        Map<Long, String> userNameMap = buildUserNameMap(entity);
        return toVO(entity, userNameMap);
    }

    @Override
    @Transactional
    public void create(FlowLeaveDTO dto) {
        FlowLeave entity = new FlowLeave();
        entity.setApplicantId(dto.getApplicantId());
        entity.setLeaveType(dto.getLeaveType());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        entity.setDays(dto.getDays());
        entity.setReason(dto.getReason());
        entity.setStatus("pending");
        flowLeaveMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, FlowLeaveDTO dto) {
        FlowLeave entity = flowLeaveMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("请假记录不存在");
        }
        entity.setApplicantId(dto.getApplicantId());
        entity.setLeaveType(dto.getLeaveType());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        entity.setDays(dto.getDays());
        entity.setReason(dto.getReason());
        flowLeaveMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        flowLeaveMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void approve(FlowApproveDTO dto) {
        FlowLeave entity = flowLeaveMapper.selectById(dto.getId());
        if (entity == null) {
            throw new RuntimeException("请假记录不存在");
        }
        entity.setStatus(dto.getResult());
        entity.setApproverId(SecurityUtils.getCurrentUserId());
        entity.setApproveTime(LocalDateTime.now());
        entity.setApproveComment(dto.getComment());
        flowLeaveMapper.updateById(entity);
    }

    private Map<Long, String> buildUserNameMap(FlowLeave entity) {
        List<Long> ids = java.util.stream.Stream.of(entity.getApplicantId(), entity.getApproverId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return ids.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(ids).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
    }

    private FlowLeaveVO toVO(FlowLeave entity, Map<Long, String> userNameMap) {
        FlowLeaveVO vo = new FlowLeaveVO();
        vo.setId(entity.getId());
        vo.setApplicantId(entity.getApplicantId());
        vo.setApplicantName(userNameMap.get(entity.getApplicantId()));
        vo.setLeaveType(entity.getLeaveType());
        vo.setStartDate(entity.getStartDate() != null ? entity.getStartDate().toString() : null);
        vo.setEndDate(entity.getEndDate() != null ? entity.getEndDate().toString() : null);
        vo.setDays(entity.getDays());
        vo.setReason(entity.getReason());
        vo.setStatus(entity.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.get(entity.getStatus()));
        vo.setApproverId(entity.getApproverId());
        vo.setApproverName(entity.getApproverId() != null ? userNameMap.get(entity.getApproverId()) : null);
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
