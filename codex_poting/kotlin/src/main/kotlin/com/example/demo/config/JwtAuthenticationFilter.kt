package com.example.demo.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthenticationFilter(private val jwtTokenService: JwtTokenService) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader("Authorization")
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }
        val token: String = authorization.substring(BEARER_PREFIX.length)
        try {
            val authentication = jwtTokenService.toAuthentication(token)
            SecurityContextHolder.getContext().setAuthentication(authentication)
        } catch (ex: IllegalArgumentException) {
            SecurityContextHolder.clearContext()
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid bearer token")
            return
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
