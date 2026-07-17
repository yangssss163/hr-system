package com.hr.module.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowExpenseDTO;
import com.hr.module.workflow.dto.FlowExpenseQuery;
import com.hr.module.workflow.dto.FlowExpenseVO;
import com.hr.module.workflow.entity.FlowExpense;
import com.hr.module.workflow.mapper.FlowExpenseMapper;
import com.hr.module.workflow.service.FlowExpenseService;
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
public class FlowExpenseServiceImpl implements FlowExpenseService {

    private static final Map<String, String> STATUS_NAME_MAP = Map.of(
            "pending", "待审批",
            "approved", "已批准",
            "rejected", "已拒绝"
    );

    private final FlowExpenseMapper flowExpenseMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<FlowExpenseVO> page(FlowExpenseQuery query) {
        LambdaQueryWrapper<FlowExpense> wrapper = new LambdaQueryWrapper<>();
        if (query.getApplicantId() != null) {
            wrapper.eq(FlowExpense::getApplicantId, query.getApplicantId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(FlowExpense::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(FlowExpense::getCreateTime);

        Page<FlowExpense> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<FlowExpense> result = flowExpenseMapper.selectPage(page, wrapper);

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
    public FlowExpenseVO getById(Long id) {
        FlowExpense entity = flowExpenseMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        Map<Long, String> userNameMap = buildUserNameMap(entity);
        return toVO(entity, userNameMap);
    }

    @Override
    @Transactional
    public void create(FlowExpenseDTO dto) {
        FlowExpense entity = new FlowExpense();
        entity.setApplicantId(dto.getApplicantId());
        entity.setAmount(dto.getAmount());
        entity.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        entity.setStatus("pending");
        flowExpenseMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, FlowExpenseDTO dto) {
        FlowExpense entity = flowExpenseMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("报销记录不存在");
        }
        entity.setApplicantId(dto.getApplicantId());
        entity.setAmount(dto.getAmount());
        entity.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
        entity.setCategory(dto.getCategory());
        entity.setDescription(dto.getDescription());
        flowExpenseMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        flowExpenseMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void approve(FlowApproveDTO dto) {
        FlowExpense entity = flowExpenseMapper.selectById(dto.getId());
        if (entity == null) {
            throw new RuntimeException("报销记录不存在");
        }
        entity.setStatus(dto.getResult());
        entity.setApproverId(SecurityUtils.getCurrentUserId());
        entity.setApproveTime(LocalDateTime.now());
        entity.setApproveComment(dto.getComment());
        flowExpenseMapper.updateById(entity);
    }

    private Map<Long, String> buildUserNameMap(FlowExpense entity) {
        List<Long> ids = java.util.stream.Stream.of(entity.getApplicantId(), entity.getApproverId())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        return ids.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(ids).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));
    }

    private FlowExpenseVO toVO(FlowExpense entity, Map<Long, String> userNameMap) {
        FlowExpenseVO vo = new FlowExpenseVO();
        vo.setId(entity.getId());
        vo.setApplicantId(entity.getApplicantId());
        vo.setApplicantName(userNameMap.get(entity.getApplicantId()));
        vo.setAmount(entity.getAmount());
        vo.setExpenseDate(entity.getExpenseDate() != null ? entity.getExpenseDate().toString() : null);
        vo.setCategory(entity.getCategory());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setStatusName(STATUS_NAME_MAP.get(entity.getStatus()));
        vo.setApproverId(entity.getApproverId());
        vo.setApproverName(entity.getApproverId() != null ? userNameMap.get(entity.getApproverId()) : null);
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
