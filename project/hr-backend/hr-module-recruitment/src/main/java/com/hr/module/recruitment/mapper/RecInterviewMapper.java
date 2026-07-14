package com.hr.module.recruitment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hr.module.recruitment.entity.RecInterview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecInterviewMapper extends BaseMapper<RecInterview> {

    @Select("SELECT result, COUNT(*) as count FROM rec_interview GROUP BY result")
    List<Map<String, Object>> countByResult();

    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m') as month, COUNT(*) as count " +
            "FROM rec_interview " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m') " +
            "ORDER BY month")
    List<Map<String, Object>> countMonthly();

    @Select("SELECT r.source as channel, COUNT(*) as count " +
            "FROM rec_interview i " +
            "LEFT JOIN rec_resume r ON i.resume_id = r.id " +
            "WHERE r.source IS NOT NULL " +
            "GROUP BY r.source")
    List<Map<String, Object>> countByChannel();
}
