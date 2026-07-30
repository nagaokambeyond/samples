package com.example.demo.api.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

class LoginResponse {
    @field:NotNull
    @field:Schema(description = "ユーザー名", type = "string")
    var username: String? = null

    @field:NotNull
    @field:Schema(description = "トークン種別", type = "string", example = "Bearer")
    var tokenType: String? = null

    @field:NotNull
    @field:Schema(description = "アクセストークン", type = "string")
    var accessToken: String? = null

    @field:NotNull
    @field:Schema(description = "有効期限（秒）", type = "integer", format = "int64")
    var expiresIn: Long? = null
}
