package com.hr.framework.web;

import com.hr.common.result.Result;
import com.hr.framework.util.MinioUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "公共服务")
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
public class CommonController {

    private final MinioUtils minioUtils;

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "module", required = false) String module) {
        try {
            return Result.success(minioUtils.upload(file, module));
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }

    @Operation(summary = "文件下载")
    @GetMapping("/download/{fileKey}")
    public void download(@PathVariable String fileKey, HttpServletResponse response) throws Exception {
        minioUtils.download(fileKey, response);
    }

    @Operation(summary = "获取下拉选项数据")
    @GetMapping("/options")
    public Result<List<Map<String, Object>>> getOptions(@RequestParam("type") String type) {
        return Result.success(getOptionsByType(type));
    }

    /**
     * 根据类型返回下拉选项
     */
    private List<Map<String, Object>> getOptionsByType(String type) {
        List<Map<String, Object>> options = new ArrayList<>();

        switch (type) {
            case "education" -> {
                addOption(options, "high_school", "高中");
                addOption(options, "junior_college", "大专");
                addOption(options, "bachelor", "本科");
                addOption(options, "master", "硕士");
                addOption(options, "doctor", "博士");
            }
            case "gender" -> {
                addOption(options, 1, "男");
                addOption(options, 2, "女");
            }
            case "resume_source" -> {
                addOption(options, "boss", "BOSS直聘");
                addOption(options, "lagou", "拉勾");
                addOption(options, "zhilian", "智联招聘");
                addOption(options, "51job", "前程无忧");
                addOption(options, "liepin", "猎聘");
                addOption(options, "internal", "内部推荐");
                addOption(options, "campus", "校园招聘");
                addOption(options, "other", "其他");
            }
            case "leave_type" -> {
                addOption(options, "annual", "年假");
                addOption(options, "sick", "病假");
                addOption(options, "personal", "事假");
                addOption(options, "marriage", "婚假");
                addOption(options, "maternity", "产假");
                addOption(options, "bereavement", "丧假");
                addOption(options, "compensatory", "调休");
            }
            case "transfer_type" -> {
                addOption(options, "regular", "转正");
                addOption(options, "transfer", "调岗");
                addOption(options, "promote", "晋升");
                addOption(options, "dimission", "离职");
                addOption(options, "rehire", "返聘");
            }
            default -> {
                return options;
            }
        }
        return options;
    }

    private void addOption(List<Map<String, Object>> list, Object value, String label) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("value", value);
        item.put("label", label);
        list.add(item);
    }
}
