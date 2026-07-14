package com.hr.module.recruitment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.common.enums.ResultCode;
import com.hr.common.exception.BusinessException;
import com.hr.module.recruitment.dto.QuestionDTO;
import com.hr.module.recruitment.dto.QuestionQuery;
import com.hr.module.recruitment.dto.QuestionVO;
import com.hr.module.recruitment.entity.RecQuestion;
import com.hr.module.recruitment.mapper.RecQuestionMapper;
import com.hr.module.recruitment.service.RecQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecQuestionServiceImpl implements RecQuestionService {

    private final RecQuestionMapper recQuestionMapper;

    private static final Map<String, String> CATEGORY_NAMES = new LinkedHashMap<>();
    private static final Map<String, String> DIFFICULTY_NAMES = new LinkedHashMap<>();

    static {
        CATEGORY_NAMES.put("technical", "技术");
        CATEGORY_NAMES.put("behavioral", "行为");
        CATEGORY_NAMES.put("hr", "综合");
        DIFFICULTY_NAMES.put("easy", "简单");
        DIFFICULTY_NAMES.put("medium", "中等");
        DIFFICULTY_NAMES.put("hard", "困难");
    }

    @Override
    public IPage<QuestionVO> page(QuestionQuery query) {
        LambdaQueryWrapper<RecQuestion> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(RecQuestion::getTitle, query.getKeyword());
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(RecQuestion::getCategory, query.getCategory());
        }
        if (StringUtils.hasText(query.getDifficulty())) {
            wrapper.eq(RecQuestion::getDifficulty, query.getDifficulty());
        }
        wrapper.orderByDesc(RecQuestion::getCreateTime);

        Page<RecQuestion> page = new Page<>(query.getPage(), query.getPageSize());
        Page<RecQuestion> result = recQuestionMapper.selectPage(page, wrapper);

        List<QuestionVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<QuestionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public QuestionVO getById(Long id) {
        RecQuestion entity = recQuestionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "试题不存在");
        }
        return toVO(entity);
    }

    @Override
    @Transactional
    public void create(QuestionDTO dto) {
        RecQuestion entity = new RecQuestion();
        entity.setCategory(dto.getCategory());
        entity.setDifficulty(dto.getDifficulty());
        entity.setTitle(dto.getTitle());
        entity.setAnswer(dto.getAnswer());
        recQuestionMapper.insert(entity);
    }

    @Override
    @Transactional
    public void update(Long id, QuestionDTO dto) {
        RecQuestion entity = recQuestionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "试题不存在");
        }
        entity.setCategory(dto.getCategory());
        entity.setDifficulty(dto.getDifficulty());
        entity.setTitle(dto.getTitle());
        entity.setAnswer(dto.getAnswer());
        recQuestionMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RecQuestion entity = recQuestionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "试题不存在");
        }
        recQuestionMapper.deleteById(id);
    }

    private QuestionVO toVO(RecQuestion entity) {
        QuestionVO vo = new QuestionVO();
        vo.setId(entity.getId());
        vo.setCategory(entity.getCategory());
        vo.setCategoryName(CATEGORY_NAMES.getOrDefault(entity.getCategory(), entity.getCategory()));
        vo.setDifficulty(entity.getDifficulty());
        vo.setDifficultyName(DIFFICULTY_NAMES.getOrDefault(entity.getDifficulty(), entity.getDifficulty()));
        vo.setTitle(entity.getTitle());
        vo.setAnswer(entity.getAnswer());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
