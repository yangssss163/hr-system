package com.hr.module.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hr.module.attendance.dto.AttExceptionQuery;
import com.hr.module.attendance.dto.AttExceptionVO;
import com.hr.module.attendance.entity.AttRecord;
import com.hr.module.attendance.mapper.AttRecordMapper;
import com.hr.module.attendance.service.AttExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttExceptionServiceImpl implements AttExceptionService {

    private static final Map<String, String> TYPE_NAME_MAP = new HashMap<>();

    static {
        TYPE_NAME_MAP.put("late", "迟到");
        TYPE_NAME_MAP.put("early", "早退");
        TYPE_NAME_MAP.put("absent", "缺勤");
        TYPE_NAME_MAP.put("overtime", "加班");
    }

    private final AttRecordMapper attRecordMapper;

    @Override
    public IPage<AttExceptionVO> page(AttExceptionQuery query) {
        LambdaQueryWrapper<AttRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ne(AttRecord::getStatus, "normal");
        if (StringUtils.hasText(query.getDateStart())) {
            wrapper.ge(AttRecord::getRecordDate, LocalDate.parse(query.getDateStart()));
        }
        if (StringUtils.hasText(query.getDateEnd())) {
            wrapper.le(AttRecord::getRecordDate, LocalDate.parse(query.getDateEnd()));
        }
        wrapper.orderByDesc(AttRecord::getRecordDate);

        Page<AttRecord> page = new Page<>(query.getPage(), query.getPageSize());
        Page<AttRecord> result = attRecordMapper.selectPage(page, wrapper);

        List<AttExceptionVO> voList = result.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        Page<AttExceptionVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private AttExceptionVO toVO(AttRecord entity) {
        AttExceptionVO vo = new AttExceptionVO();
        vo.setId(entity.getId());
        vo.setEmployeeId(entity.getEmployeeId());
        vo.setRecordDate(entity.getRecordDate());
        vo.setType(entity.getStatus());
        vo.setTypeName(TYPE_NAME_MAP.getOrDefault(entity.getStatus(), entity.getStatus()));
        vo.setDetail("");
        vo.setOaStatus("");
        return vo;
    }
}
