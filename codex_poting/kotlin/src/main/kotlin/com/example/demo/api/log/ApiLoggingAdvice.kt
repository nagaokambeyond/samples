package com.example.demo.api.log

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.jspecify.annotations.NullMarked
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.Type
import java.util.*

@RestControllerAdvice
@NullMarked
class ApiLoggingAdvice : RequestBodyAdviceAdapter(), ResponseBodyAdvice<Any> {
    private val objectMapper: ObjectMapper = ObjectMapper().findAndRegisterModules()

    override fun supports(
        methodParameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Boolean {
        return this.isApiRequest
    }

    override fun afterBodyRead(
        body: Any,
        inputMessage: HttpInputMessage,
        parameter: MethodParameter,
        targetType: Type,
        converterType: Class<out HttpMessageConverter<*>>
    ): Any {
        log.info("[API REQUEST BODY] {}", toJson(body))
        return body
    }

    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse
    ): Any? {
        if (isApiRequest(request)) {
            log.info("[API RESPONSE BODY] {}", toJson(body))
        }
        return body
    }

    private val isApiRequest: Boolean
        get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes()
            if (requestAttributes !is ServletRequestAttributes) {
                return false
            }
            return isApiRequest(requestAttributes.getRequest())
        }

    private fun isApiRequest(request: HttpServletRequest): Boolean {
        return request.getRequestURI().startsWith("/api/")
    }

    private fun isApiRequest(request: ServerHttpRequest): Boolean {
        return request.getURI().getPath().startsWith("/api/")
    }

    private fun toJson(body: Any?): String {
        if (Objects.isNull(body)) {
            return "null"
        }
        try {
            return objectMapper.writeValueAsString(body)
        } catch (e: JsonProcessingException) {
            return body.toString()
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApiLoggingAdvice::class.java)
    }
}
