package com.example.demo.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginResponse {
    @NotNull
    @Schema(description = "ユーザー名", type = "string")
    private String username;

    @NotNull
    @Schema(description = "トークン種別", type = "string", example = "Bearer")
    private String tokenType;

    @NotNull
    @Schema(description = "アクセストークン", type = "string")
    private String accessToken;

    @NotNull
    @Schema(description = "有効期限（秒）", type = "integer", format = "int64")
    private Long expiresIn;
}
