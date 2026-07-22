package com.hr.module.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.recruitment.dto.*;
import com.hr.module.recruitment.entity.RecResume;
import com.hr.module.recruitment.mapper.RecResumeMapper;
import com.hr.module.recruitment.service.RecResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecResumeServiceImpl implements RecResumeService {

    private final RecResumeMapper recResumeMapper;

    @Override
    public IPage<ResumeVO> page(ResumeQuery query) {
        LambdaQueryWrapper<RecResume> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(RecResume::getName, query.getKeyword())
                    .or().like(RecResume::getPhone, query.getKeyword()));
        }
        if (StringUtils.hasText(query.getStatus())) {
            wrapper.eq(RecResume::getStatus, query.getStatus());
        }
        if (StringUtils.hasText(query.getEducation())) {
            wrapper.eq(RecResume::getEducation, query.getEducation());
        }
        if (StringUtils.hasText(query.getSource())) {
            wrapper.eq(RecResume::getSource, query.getSource());
        }
        if (StringUtils.hasText(query.getApplyPosition())) {
            wrapper.eq(RecResume::getApplyPosition, query.getApplyPosition());
        }
        if (StringUtils.hasText(query.getCreateTimeStart())) {
            wrapper.ge(RecResume::getCreateTime, LocalDate.parse(query.getCreateTimeStart()).atStartOfDay());
        }
        if (StringUtils.hasText(query.getCreateTimeEnd())) {
            wrapper.le(RecResume::getCreateTime, LocalDate.parse(query.getCreateTimeEnd()).atTime(23, 59, 59));
        }
        wrapper.orderByDesc(RecResume::getCreateTime);

        Page<RecResume> page = new Page<>(query.getPage(), query.getPageSize());
        Page<RecResume> result = recResumeMapper.selectPage(page, wrapper);

        List<ResumeVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<ResumeVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ResumeVO getById(Long id) {
        RecResume entity = recResumeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(ResumeDTO dto) {
        RecResume entity = new RecResume();
        applyDTO(entity, dto);
        entity.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "new");
        recResumeMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, ResumeDTO dto) {
        RecResume entity = recResumeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        applyDTO(entity, dto);
        recResumeMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RecResume entity = recResumeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        recResumeMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        recResumeMapper.deleteBatchIds(ids);
    }

    @Override
    @Transactional
    public void invite(Long id, InterviewInviteDTO dto) {
        RecResume entity = recResumeMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        entity.setStatus("interview");
        recResumeMapper.updateById(entity);
    }

    private void applyDTO(RecResume entity, ResumeDTO dto) {
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setGender(dto.getGender());
        entity.setEducation(dto.getEducation());
        entity.setSchool(dto.getSchool());
        entity.setMajor(dto.getMajor());
        entity.setWorkYears(dto.getWorkYears());
        entity.setApplyPosition(dto.getApplyPosition());
        entity.setSource(dto.getSource());
        entity.setResumeFile(dto.getResumeFile());
        entity.setResumeContent(dto.getResumeContent());
    }

    private ResumeVO toVO(RecResume entity) {
        ResumeVO vo = new ResumeVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPhone(entity.getPhone());
        vo.setEmail(entity.getEmail());
        vo.setGender(entity.getGender());
        vo.setEducation(entity.getEducation());
        vo.setSchool(entity.getSchool());
        vo.setMajor(entity.getMajor());
        vo.setWorkYears(entity.getWorkYears());
        vo.setApplyPosition(entity.getApplyPosition());
        vo.setSource(entity.getSource());
        vo.setStatus(entity.getStatus());
        vo.setResumeFile(entity.getResumeFile());
        vo.setResumeContent(entity.getResumeContent());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
