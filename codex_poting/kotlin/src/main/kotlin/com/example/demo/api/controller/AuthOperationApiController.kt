package com.example.demo.api.controller

import com.example.demo.api.AuthOperationApi
import com.example.demo.api.request.LoginRequest
import com.example.demo.api.response.LoginResponse
import com.example.demo.config.JwtTokenService
import com.example.demo.config.LoginRateLimitService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
class AuthOperationApiController(
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenService: JwtTokenService,
    private val loginRateLimitService: LoginRateLimitService
) : AuthOperationApi {
    override fun login(@Valid @NotNull request: @Valid @NotNull LoginRequest): LoginResponse {
        loginRateLimitService.consume(request.username)
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.username,
                request.password
            )
        )
        val response = LoginResponse()
        response.username = authentication.getName()
        response.tokenType = "Bearer"
        response.accessToken = jwtTokenService.createToken(authentication)
        response.expiresIn = jwtTokenService.expiresInSeconds
        return response
    }

    override fun resetLoginRateLimit() {
        loginRateLimitService.resetAll()
    }
}
