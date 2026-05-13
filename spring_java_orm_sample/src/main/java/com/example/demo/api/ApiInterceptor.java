package com.example.demo.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
@Slf4j
public class ApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        long start = System.currentTimeMillis();
        request.setAttribute("startTime", start);

        log.info("▶️[START] {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, Exception ex) {
        long start = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - start;

        int status = response.getStatus();

        if (ex != null) {
            log.error("❌[END] {} {} -> {} ({} ms) ERROR: {}",
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration,
                ex.getMessage());
        } else {
            log.info("✅[END] {} {} -> {} ({} ms)",
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration);
        }
    }
}
