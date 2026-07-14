package com.hr.module.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.framework.security.SecurityUtils;
import com.hr.module.workflow.dto.FlowApproveDTO;
import com.hr.module.workflow.dto.FlowLoanDTO;
import com.hr.module.workflow.dto.FlowLoanQuery;
import com.hr.module.workflow.dto.FlowLoanVO;
import com.hr.module.workflow.entity.FlowLoan;
import com.hr.module.workflow.mapper.FlowLoanMapper;
import com.hr.module.workflow.service.FlowLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FlowLoanServiceImpl implements FlowLoanService {

    private final FlowLoanMapper flowLoanMapper;

    @Override
    public IPage<FlowLoanVO> page(FlowLoanQuery query) {
        LambdaQueryWrapper<FlowLoan> wrapper = new LambdaQueryWrapper<>();
        if (query.getApplicantId() != null) {
            wrapper.eq(FlowLoan::getApplicantId, query.getApplicantId());
        }
        if (query.getStatus() != null && !query.getStatus().isBlank()) {
            wrapper.eq(FlowLoan::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(FlowLoan::getCreateTime);

        Page<FlowLoan> page = new Page<>(query.getPage(), query.getPageSize());
        IPage<FlowLoan> result = flowLoanMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public FlowLoanVO getById(Long id) {
        FlowLoan entity = flowLoanMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(FlowLoanDTO dto) {
        FlowLoan entity = new FlowLoan();
        entity.setApplicantId(dto.getApplicantId());
        entity.setAmount(dto.getAmount());
        entity.setLoanDate(LocalDate.parse(dto.getLoanDate()));
        entity.setReason(dto.getReason());
        entity.setRepaymentDate(dto.getRepaymentDate() != null ? LocalDate.parse(dto.getRepaymentDate()) : null);
        entity.setStatus("pending");
        flowLoanMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, FlowLoanDTO dto) {
        FlowLoan entity = flowLoanMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("借款记录不存在");
        }
        entity.setApplicantId(dto.getApplicantId());
        entity.setAmount(dto.getAmount());
        entity.setLoanDate(LocalDate.parse(dto.getLoanDate()));
        entity.setReason(dto.getReason());
        entity.setRepaymentDate(dto.getRepaymentDate() != null ? LocalDate.parse(dto.getRepaymentDate()) : null);
        flowLoanMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        flowLoanMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void approve(FlowApproveDTO dto) {
        FlowLoan entity = flowLoanMapper.selectById(dto.getId());
        if (entity == null) {
            throw new RuntimeException("借款记录不存在");
        }
        entity.setStatus(dto.getResult());
        entity.setApproverId(SecurityUtils.getCurrentUserId());
        entity.setApproveTime(LocalDateTime.now());
        entity.setApproveComment(dto.getComment());
        flowLoanMapper.updateById(entity);
    }

    private FlowLoanVO toVO(FlowLoan entity) {
        FlowLoanVO vo = new FlowLoanVO();
        vo.setId(entity.getId());
        vo.setApplicantId(entity.getApplicantId());
        vo.setAmount(entity.getAmount());
        vo.setLoanDate(entity.getLoanDate() != null ? entity.getLoanDate().toString() : null);
        vo.setReason(entity.getReason());
        vo.setRepaymentDate(entity.getRepaymentDate() != null ? entity.getRepaymentDate().toString() : null);
        vo.setStatus(entity.getStatus());
        vo.setApproverId(entity.getApproverId());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
