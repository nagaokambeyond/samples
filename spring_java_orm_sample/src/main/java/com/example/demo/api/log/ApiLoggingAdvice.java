package com.example.demo.api.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
@NullMarked
public class ApiLoggingAdvice extends RequestBodyAdviceAdapter implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public boolean supports(
        MethodParameter methodParameter,
        Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return isApiRequest();
    }

    @Override
    public Object afterBodyRead(
        Object body,
        HttpInputMessage inputMessage,
        MethodParameter parameter,
        Type targetType,
        Class<? extends HttpMessageConverter<?>> converterType
    ) {
        log.info("[API REQUEST BODY] {}", toJson(body));
        return body;
    }

    @Override
    public boolean supports(
        MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public @Nullable Object beforeBodyWrite(
        @Nullable Object body,
        MethodParameter returnType,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request,
        ServerHttpResponse response
    ) {
        if (isApiRequest(request)) {
            log.info("[API RESPONSE BODY] {}", toJson(body));
        }
        return body;
    }

    private boolean isApiRequest() {
        final var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return false;
        }
        return isApiRequest(servletRequestAttributes.getRequest());
    }

    private boolean isApiRequest(final HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private boolean isApiRequest(final ServerHttpRequest request) {
        return request.getURI().getPath().startsWith("/api/");
    }

    private String toJson(final @Nullable Object body) {
        if (Objects.isNull(body)) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return String.valueOf(body);
        }
    }
}
