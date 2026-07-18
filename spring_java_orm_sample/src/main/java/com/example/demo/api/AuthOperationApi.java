package com.example.demo.api;

import com.example.demo.api.request.LoginRequest;
import com.example.demo.api.response.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "認証API")
public interface AuthOperationApi {
    @PostMapping("/login")
    @Operation(summary = "ログイン")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "成功",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "リクエストエラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "invalidRequestBody",
                    summary = "リクエストボディのバリデーションエラー",
                    value = """
                        {
                          "errors": [
                            {
                              "field": "username",
                              "message": "空白は許可されていません"
                            }
                          ],
                          "instance": "/api/auth/login",
                          "status": 400,
                          "title": "リクエストバリデーションエラー"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "認証エラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "badCredentials",
                    summary = "ユーザー名またはパスワードが不正",
                    value = """
                        {
                          "detail": "ユーザー名またはパスワードが不正です",
                          "instance": "/api/auth/login",
                          "status": 401,
                          "title": "認証エラー"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "リクエスト回数制限",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class),
                examples = @ExampleObject(
                    name = "loginRateLimitExceeded",
                    summary = "ログインリクエスト回数の日次上限超過",
                    value = """
                        {
                          "detail": "ログインリクエスト回数が日次上限を超えました",
                          "instance": "/api/auth/login",
                          "status": 429,
                          "title": "リクエスト回数制限"
                        }
                        """
                )
            )
        )
    })
    LoginResponse login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ログインリクエスト",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "loginRequest",
                    summary = "ログインリクエスト",
                    value = """
                        {
                          "username": "admin",
                          "password": "password"
                        }
                        """
                )
            )
        )
        @RequestBody @Valid @NotNull LoginRequest request
    );

    @PostMapping("/login-rate-limit/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "ログインリクエスト回数制限のリセット", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "リセット成功"),
        @ApiResponse(
            responseCode = "401",
            description = "認証エラー",
            content = @Content(
                mediaType = "application/problem+json",
                schema = @Schema(implementation = ProblemDetail.class)
            )
        )
    })
    void resetLoginRateLimit();
}
