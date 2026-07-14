package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.attendance.dto.AttOaFlowQuery;
import com.hr.module.attendance.dto.AttOaFlowVO;
import com.hr.module.attendance.entity.AttOaFlow;
import com.hr.module.attendance.mapper.AttOaFlowMapper;
import com.hr.module.attendance.service.AttOaFlowService;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttOaFlowServiceImpl implements AttOaFlowService {

    private final AttOaFlowMapper attOaFlowMapper;

    @Override
    public IPage<AttOaFlowVO> page(AttOaFlowQuery query) {
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
        Page<AttOaFlowVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public AttOaFlowVO getById(Long id) {
        AttOaFlow entity = attOaFlowMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "OA流程不存在");
        }
        return toVO(entity);
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
