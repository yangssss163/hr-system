package com.hr.module.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.recruitment.dto.*;
import com.hr.module.recruitment.entity.RecInterview;
import com.hr.module.recruitment.entity.RecResume;
import com.hr.module.recruitment.mapper.RecInterviewMapper;
import com.hr.module.recruitment.mapper.RecResumeMapper;
import com.hr.module.recruitment.service.RecInterviewService;
import com.hr.module.system.entity.SysUser;
import com.hr.module.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecInterviewServiceImpl implements RecInterviewService {

    private final RecInterviewMapper recInterviewMapper;
    private final RecResumeMapper recResumeMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    public IPage<InterviewVO> page(InterviewQuery query) {
        LambdaQueryWrapper<RecInterview> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getResult())) {
            wrapper.eq(RecInterview::getResult, query.getResult());
        }
        if (query.getInterviewerId() != null) {
            wrapper.eq(RecInterview::getInterviewerId, query.getInterviewerId());
        }
        if (StringUtils.hasText(query.getInterviewDateStart())) {
            wrapper.ge(RecInterview::getInterviewTime, LocalDateTime.parse(query.getInterviewDateStart() + "T00:00:00"));
        }
        if (StringUtils.hasText(query.getInterviewDateEnd())) {
            wrapper.le(RecInterview::getInterviewTime, LocalDateTime.parse(query.getInterviewDateEnd() + "T23:59:59"));
        }
        wrapper.orderByDesc(RecInterview::getCreateTime);

        Page<RecInterview> page = new Page<>(query.getPage(), query.getPageSize());
        Page<RecInterview> result = recInterviewMapper.selectPage(page, wrapper);

        // 批量查询关联数据
        List<Long> resumeIds = result.getRecords().stream()
                .map(RecInterview::getResumeId).distinct().collect(Collectors.toList());
        List<Long> interviewerIds = result.getRecords().stream()
                .map(RecInterview::getInterviewerId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        Map<Long, RecResume> resumeMap = resumeIds.isEmpty() ? Map.of() :
                recResumeMapper.selectBatchIds(resumeIds).stream()
                        .collect(Collectors.toMap(RecResume::getId, r -> r));
        Map<Long, String> interviewerNameMap = interviewerIds.isEmpty() ? Map.of() :
                sysUserMapper.selectBatchIds(interviewerIds).stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getRealName));

        // 过滤关键词
        List<RecInterview> records = result.getRecords();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword();
            records = records.stream()
                    .filter(i -> {
                        RecResume r = resumeMap.get(i.getResumeId());
                        return r != null && r.getName() != null && r.getName().contains(keyword);
                    })
                    .collect(Collectors.toList());
        }

        List<InterviewVO> voList = records.stream()
                .map(i -> toVO(i, resumeMap, interviewerNameMap))
                .collect(Collectors.toList());

        Page<InterviewVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public InterviewVO getById(Long id) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        RecResume resume = recResumeMapper.selectById(entity.getResumeId());
        String interviewerName = null;
        if (entity.getInterviewerId() != null) {
            SysUser user = sysUserMapper.selectById(entity.getInterviewerId());
            interviewerName = user != null ? user.getRealName() : null;
        }
        return toVO(entity, resume, interviewerName);
    }

    @Override
    @Transactional
    public void create(InterviewDTO dto) {
        RecResume resume = recResumeMapper.selectById(dto.getResumeId());
        if (resume == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "简历不存在");
        }
        RecInterview entity = new RecInterview();
        entity.setResumeId(dto.getResumeId());
        entity.setInterviewRound(dto.getInterviewRound());
        entity.setInterviewerId(dto.getInterviewerId());
        entity.setInterviewTime(LocalDateTime.parse(dto.getInterviewTime().replace(" ", "T")));
        entity.setLocation(dto.getLocation());
        entity.setResult("pending");
        recInterviewMapper.insert(entity);

        // 更新简历状态为面试中
        resume.setStatus("interview");
        recResumeMapper.updateById(resume);
    }

    @Override
    @Transactional
    public void updateTime(Long id, InterviewDTO dto) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setInterviewTime(LocalDateTime.parse(dto.getInterviewTime().replace(" ", "T")));
        entity.setLocation(dto.getLocation());
        if (dto.getInterviewerId() != null) {
            entity.setInterviewerId(dto.getInterviewerId());
        }
        recInterviewMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void checkin(Long id) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        // 签到暂不做状态变更，仅记录
    }

    @Override
    @Transactional
    public void evaluate(Long id, InterviewEvaluateDTO dto) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setScore(dto.getScore());
        entity.setEvaluation(dto.getEvaluation());
        if (StringUtils.hasText(dto.getResult())) {
            entity.setResult(dto.getResult());
        }
        recInterviewMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void pass(Long id) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setResult("pass");
        recInterviewMapper.updateById(entity);

        RecResume resume = recResumeMapper.selectById(entity.getResumeId());
        if (resume != null) {
            resume.setStatus("interview");
            recResumeMapper.updateById(resume);
        }
    }

    @Override
    @Transactional
    public void eliminate(Long id, InterviewEliminateDTO dto) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setResult("fail");
        entity.setReason(dto.getReason());
        recInterviewMapper.updateById(entity);

        // 同步更新简历状态为淘汰
        RecResume resume = recResumeMapper.selectById(entity.getResumeId());
        if (resume != null) {
            resume.setStatus("eliminated");
            recResumeMapper.updateById(resume);
        }
    }

    @Override
    @Transactional
    public void offer(Long id, InterviewOfferDTO dto) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setResult("offer");
        entity.setOfferSalary(dto.getOfferSalary());
        if (StringUtils.hasText(dto.getOfferDate())) {
            entity.setOfferDate(LocalDate.parse(dto.getOfferDate()));
        }
        entity.setOfferRemark(dto.getRemark());
        recInterviewMapper.updateById(entity);

        // 同步更新简历状态
        RecResume resume = recResumeMapper.selectById(entity.getResumeId());
        if (resume != null) {
            resume.setStatus("offer");
            recResumeMapper.updateById(resume);
        }
    }

    @Override
    @Transactional
    public void confirmHire(Long id) {
        RecInterview entity = recInterviewMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "面试记录不存在");
        }
        entity.setResult("hired");
        recInterviewMapper.updateById(entity);

        // 同步更新简历状态为已入职
        RecResume resume = recResumeMapper.selectById(entity.getResumeId());
        if (resume != null) {
            resume.setStatus("hired");
            recResumeMapper.updateById(resume);
        }
    }

    private InterviewVO toVO(RecInterview entity, RecResume resume, String interviewerName) {
        InterviewVO vo = new InterviewVO();
        vo.setId(entity.getId());
        vo.setResumeId(entity.getResumeId());
        if (resume != null) {
            vo.setCandidateName(resume.getName());
            vo.setApplyPosition(resume.getApplyPosition());
        }
        vo.setInterviewRound(entity.getInterviewRound());
        vo.setInterviewerId(entity.getInterviewerId());
        vo.setInterviewerName(interviewerName);
        vo.setInterviewTime(entity.getInterviewTime());
        vo.setLocation(entity.getLocation());
        vo.setResult(entity.getResult());
        vo.setScore(entity.getScore());
        vo.setEvaluation(entity.getEvaluation());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }

    private InterviewVO toVO(RecInterview entity, Map<Long, RecResume> resumeMap, Map<Long, String> interviewerNameMap) {
        RecResume resume = resumeMap.get(entity.getResumeId());
        String interviewerName = entity.getInterviewerId() != null
                ? interviewerNameMap.get(entity.getInterviewerId()) : null;
        return toVO(entity, resume, interviewerName);
    }
}
