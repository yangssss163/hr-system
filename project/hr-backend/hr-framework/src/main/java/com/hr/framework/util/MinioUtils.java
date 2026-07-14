package com.hr.framework.util;

import com.hr.framework.config.MinioConfig;
import io.minio.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtils {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 文件上传
     * @param file   上传文件
     * @param module 模块名（如 resume、employee），用于分类存储
     * @return 文件信息（fileName, fileKey, fileUrl, fileSize）
     */
    public Map<String, Object> upload(MultipartFile file, String module) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String modulePath = (module != null && !module.isBlank()) ? module : "common";
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String objectName = modulePath + "/" + datePath + "/" + UUID.randomUUID() + ext;

        ensureBucketExists();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        log.info("文件上传成功: bucket={}, object={}, size={}", minioConfig.getBucket(), objectName, file.getSize());

        Map<String, Object> result = new HashMap<>();
        result.put("fileName", originalFilename);
        result.put("fileKey", objectName);
        result.put("fileUrl", minioConfig.getEndpoint() + "/" + minioConfig.getBucket() + "/" + objectName);
        result.put("fileSize", file.getSize());
        return result;
    }

    /**
     * 文件下载
     * @param fileKey  文件存储路径
     * @param response HTTP 响应
     */
    public void download(String fileKey, HttpServletResponse response) throws Exception {
        GetObjectResponse object = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(minioConfig.getBucket())
                        .object(fileKey)
                        .build()
        );

        String fileName = fileKey.contains("/")
                ? fileKey.substring(fileKey.lastIndexOf("/") + 1)
                : fileKey;
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        try (InputStream is = object; OutputStream os = response.getOutputStream()) {
            is.transferTo(os);
            os.flush();
        }
        log.info("文件下载成功: bucket={}, object={}", minioConfig.getBucket(), fileKey);
    }

    /**
     * 确保 Bucket 存在，不存在则创建
     */
    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioConfig.getBucket()).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(minioConfig.getBucket()).build()
            );
            log.info("创建 MinIO Bucket: {}", minioConfig.getBucket());
        }
    }
}
