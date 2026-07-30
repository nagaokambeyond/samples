package com.example.demo.api.log

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*

@Configuration
class ApiInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val start = System.currentTimeMillis()
        request.setAttribute("startTime", start)
        log.info("▶[API START] {} {}", request.getMethod(), request.getRequestURI() + getQueryString(request))
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val start = request.getAttribute("startTime") as Long
        val duration = System.currentTimeMillis() - start
        val status = response.getStatus()
        if (ex != null) {
            log.error(
                "❌[API END] {} {} -> {} ({} ms) ERROR: {}",
                request.getMethod(),
                request.getRequestURI(),
                status,
                duration,
                ex.message
            )
        } else {
            log.info("✅[API END] {} {} -> {} ({} ms)", request.getMethod(), request.getRequestURI(), status, duration)
        }
    }

    private fun getQueryString(request: HttpServletRequest): String {
        val queryString = request.getQueryString()
        if (Objects.isNull(queryString)) {
            return ""
        }
        return "?" + queryString
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApiInterceptor::class.java)
    }
}
