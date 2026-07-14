package com.hr.framework.web;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * SPA 前端路由回退 — 非 /api/** 的 404 返回 index.html 交由 Vue Router 处理
 */
@Controller
public class SpaFallbackController {

    @RequestMapping("/spa-fallback")
    public String handleSpaFallback(HttpServletRequest request) {
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String path = uri != null ? uri.toString() : "";
        if (path.startsWith("/api/") || path.startsWith("/doc.html") || path.startsWith("/webjars/") || path.startsWith("/v3/")) {
            return null;
        }
        return "forward:/index.html";
    }

    @Configuration
    public static class SpaErrorPageRegistrar implements ErrorPageRegistrar {
        @Override
        public void registerErrorPages(ErrorPageRegistry registry) {
            registry.addErrorPages(new ErrorPage("/spa-fallback"));
        }
    }
}
