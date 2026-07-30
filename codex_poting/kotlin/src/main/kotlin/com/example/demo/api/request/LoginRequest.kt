package com.example.demo.api.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    @field:Schema(description = "ユーザー名", type = "string")
    val username: String?,

    @field:NotBlank
    @field:Schema(description = "パスワード", type = "string")
    val password: String?
)
