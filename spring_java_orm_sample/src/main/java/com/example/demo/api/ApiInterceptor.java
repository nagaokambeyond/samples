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
        final var start = System.currentTimeMillis();
        request.setAttribute("startTime", start);

        log.info("▶[API START] {} {}", request.getMethod(), request.getRequestURI() + getQueryString(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, @NonNull Object handler, Exception ex) {
        final var start = (long) request.getAttribute("startTime");
        final var duration = System.currentTimeMillis() - start;

        final var status = response.getStatus();

        if (ex != null) {
            log.error("❌[API END] {} {} -> {} ({} ms) ERROR: {}",
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration,
                ex.getMessage());
        } else {
            log.info("✅[API END] {} {} -> {} ({} ms)",
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration);
        }
    }

    private String getQueryString(final HttpServletRequest request) {
        final var queryString = request.getQueryString();
        if (queryString == null) {
            return "";
        }
        return "?" + queryString;
    }
}
