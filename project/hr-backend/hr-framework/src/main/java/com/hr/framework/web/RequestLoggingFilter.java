package com.hr.framework.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 请求日志过滤器 — 记录所有 HTTP 请求的连接信息和响应状态。
 * 日志输出到控制台，同时通过 Tomcat access log 写入文件。
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 2000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        // 包装 request/response 以便读取 body
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            int status = responseWrapper.getStatus();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            String clientIp = getClientIp(request);
            String authHeader = request.getHeader("Authorization");
            String authInfo = authHeader != null ? "Bearer***" : "none";

            String fullPath = query != null ? uri + "?" + query : uri;

            if (status >= 500) {
                log.error("请求 {} {} | 客户端: {} | 状态: {} | 耗时: {}ms | 认证: {}",
                        method, fullPath, clientIp, status, elapsed, authInfo);
                log.error("响应体: {}", getBody(responseWrapper.getContentAsByteArray()));
            } else if (status >= 400) {
                log.warn("请求 {} {} | 客户端: {} | 状态: {} | 耗时: {}ms | 认证: {}",
                        method, fullPath, clientIp, status, elapsed, authInfo);
            } else {
                log.info("请求 {} {} | 客户端: {} | 状态: {} | 耗时: {}ms | 认证: {}",
                        method, fullPath, clientIp, status, elapsed, authInfo);
            }

            if (log.isDebugEnabled() && status >= 400) {
                log.debug("请求体: {}", getBody(requestWrapper.getContentAsByteArray()));
                log.debug("响应体: {}", getBody(responseWrapper.getContentAsByteArray()));
            }

            responseWrapper.copyBodyToResponse();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private String getBody(byte[] content) {
        if (content.length == 0) {
            return "";
        }
        String body = new String(content, StandardCharsets.UTF_8);
        if (body.length() > MAX_BODY_LENGTH) {
            body = body.substring(0, MAX_BODY_LENGTH) + "...(截断)";
        }
        return body.replaceAll("\\s+", " ");
    }
}
