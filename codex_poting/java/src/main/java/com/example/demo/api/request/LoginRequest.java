package com.example.demo.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequest {
    @NotBlank
    @Schema(description = "ユーザー名", type = "string")
    String username;

    @NotBlank
    @Schema(description = "パスワード", type = "string")
    String password;
}
