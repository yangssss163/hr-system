package com.hr.module.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowTravelDTO;
import com.hr.module.workflow.dto.FlowTravelQuery;
import com.hr.module.workflow.dto.FlowTravelVO;
import com.hr.module.workflow.entity.FlowTravel;
import com.hr.module.workflow.mapper.FlowTravelMapper;
import com.hr.module.workflow.service.FlowTravelService;
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
public class FlowTravelServiceImpl implements FlowTravelService {

    private static final Map<String, String> STATUS_NAME_MAP = Map.of(
            "pending", "待审批",
            "approved", "已批准",
            "rejected", "已拒绝"
    );

    private final FlowTravelMapper flowTravelMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<FlowTravelVO> page(FlowTravelQuery query) {
        LambdaQueryWrapper<FlowTravel> wrapper = new LambdaQueryWrapper<>();
        if (query.getApplicantId() != null) {
            wrapper.eq(FlowTravel::getApplicantId, query.getApplicantId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(FlowTravel::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(FlowTravel::getCreateTime);

        Page<FlowTravel> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<FlowTravel> result = flowTravelMapper.selectPage(page, wrapper);

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
    public FlowTravelVO getById(Long id) {
        FlowTravel entity = flowTravelMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        Map<Long, String> userNameMap = buildUserNameMap(entity);
        return toVO(entity, userNameMap);
    }

    @Override
    @Transactional
    public void create(FlowTravelDTO dto) {
        FlowTravel entity = new FlowTravel();
        entity.setApplicantId(dto.getApplicantId());
        entity.setDestination(dto.getDestination());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        entity.setDays(dto.getDays());
        entity.setReason(dto.getReason());
        entity.setCompanions(dto.getCompanions());
        entity.setBudget(dto.getBudget());
        entity.setStatus("pending");
        flowTravelMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, FlowTravelDTO dto) {
        FlowTravel entity = flowTravelMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("出差记录不存在");
        }
        entity.setApplicantId(dto.getApplicantId());
        entity.setDestination(dto.getDestination());
        entity.setStartDate(LocalDate.parse(dto.getStartDate()));
        entity.setEndDate(LocalDate.parse(dto.getEndDate()));
        entity.setDays(dto.getDays());
        entity.setReason(dto.getReason());
        entity.setCompanions(dto.getCompanions());
        entity.setBudget(dto.getBudget());
        flowTravelMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        flowTravelMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void approve(FlowApproveDTO dto) {
        FlowTravel entity = flowTravelMapper.selectById(dto.getId());
        if (entity == null) {
            throw new RuntimeException("出差记录不存在");
        }
        entity.setStatus(dto.getResult());
        entity.setApproverId(SecurityUtils.getCurrentUserId());
        entity.setApproveTime(LocalDateTime.now());
        entity.setApproveComment(dto.getComment());
        flowTravelMapper.updateById(entity);
    }

    private Map<Long, String> buildUserNameMap(FlowTravel entity) {
        List<Long> ids = java.util.stream.Stream.of(entity.getApplicantId(), entity.getApproverId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return ids.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(ids).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
    }

    private FlowTravelVO toVO(FlowTravel entity, Map<Long, String> userNameMap) {
        FlowTravelVO vo = new FlowTravelVO();
        vo.setId(entity.getId());
        vo.setApplicantId(entity.getApplicantId());
        vo.setApplicantName(userNameMap.get(entity.getApplicantId()));
        vo.setDestination(entity.getDestination());
        vo.setStartDate(entity.getStartDate() != null ? entity.getStartDate().toString() : null);
        vo.setEndDate(entity.getEndDate() != null ? entity.getEndDate().toString() : null);
        vo.setDays(entity.getDays());
        vo.setReason(entity.getReason());
        vo.setCompanions(entity.getCompanions());
        vo.setBudget(entity.getBudget());
        vo.setStatus(entity.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.get(entity.getStatus()));
        vo.setApproverId(entity.getApproverId());
        vo.setApproverName(entity.getApproverId() != null ? userNameMap.get(entity.getApproverId()) : null);
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
