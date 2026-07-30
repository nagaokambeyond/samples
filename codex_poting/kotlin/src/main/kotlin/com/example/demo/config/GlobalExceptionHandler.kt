package com.example.demo.config

import com.example.demo.exception.*
import com.example.demo.openbd.generated.invoker.ApiException
import com.example.demo.util.ExceptionHandlerUtil
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSourceResolvable
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.http.*
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.validation.method.ParameterErrors
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.jspecify.annotations.NullMarked

@RestControllerAdvice
@NullMarked
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(RepositoryDataNotfoundException::class)
    fun handleRepositoryDataNotfoundException(ex: RepositoryDataNotfoundException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)

        problem.setTitle("該当データなし")

        return problem
    }

    @ExceptionHandler(OpenBdBookNotFoundException::class)
    fun handleOpenBdBookNotFoundException(ex: OpenBdBookNotFoundException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)

        problem.setTitle("OpenBD書誌なし")

        return problem
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleObjectOptimisticLockingFailureException(ex: ObjectOptimisticLockingFailureException): ProblemDetail {
        return createConflictProblem()
    }

    @ExceptionHandler(PessimisticLockingFailureException::class)
    fun handlePessimisticLockingFailureException(ex: PessimisticLockingFailureException): ProblemDetail {
        return createConflictProblem()
    }

    private fun createConflictProblem(): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.CONFLICT)

        problem.setTitle("更新競合")
        problem.setDetail("他ユーザーによって更新されています")

        return problem
    }

    @ExceptionHandler(CorrelationValidationFailureException::class)
    fun handleCorrelationValidationFailureException(ex: CorrelationValidationFailureException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)

        problem.setTitle("相関バリデーション")
        problem.setDetail(ex.message)

        return problem
    }

    @ExceptionHandler(ForeignKeyReferenceNotFoundException::class)
    fun handleForeignKeyReferenceNotFoundException(ex: ForeignKeyReferenceNotFoundException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)

        problem.setTitle("データバリデーション")
        problem.setDetail(ex.message)

        return problem
    }

    @ExceptionHandler(UniqueConstraintValidationException::class)
    fun handleUniqueConstraintValidationException(ex: UniqueConstraintValidationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)

        problem.setTitle("データバリデーション")
        problem.setDetail(ex.message)

        return problem
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)

        problem.setTitle("認証エラー")
        problem.setDetail("ユーザー名またはパスワードが不正です")

        return problem
    }

    @ExceptionHandler(LoginRateLimitExceededException::class)
    fun handleLoginRateLimitExceededException(ex: LoginRateLimitExceededException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS)

        problem.setTitle("リクエスト回数制限")
        problem.setDetail("ログインリクエスト回数が日次上限を超えました")

        return problem
    }

    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY)

        problem.setTitle("外部API呼び出しエラー")
        problem.setDetail("OpenBD APIの呼び出しに失敗しました")

        return problem
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)

        problem.setTitle("リクエストエラー")
        problem.setDetail(ex.message)
        problem.setProperty(
            "errors",
            ex.getConstraintViolations().stream()
                .map<Map<String, String>> { violation: ConstraintViolation<*>? ->
                    ExceptionHandlerUtil.createValidationError(
                        ExceptionHandlerUtil.getLastPropertyName(violation!!.getPropertyPath()),
                        violation.getMessage()
                    )
                }
                .toList()
        )

        return problem
    }

    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)

        problem.setTitle("リクエストバリデーションエラー")
        problem.setProperty(
            "errors",
            ex.getBindingResult().getFieldErrors().stream()
                .map<Map<String, String>> { error: FieldError? ->
                    ExceptionHandlerUtil.createValidationError(
                        error!!.getField(),
                        error.getDefaultMessage()
                    )
                }
                .toList()
        )

        return problem
    }

    fun handleHandlerMethodValidationException(ex: HandlerMethodValidationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        val errors = ArrayList<Map<String, String>>()

        problem.setTitle("リクエストバリデーションエラー")
        for (validationResult in ex.getParameterValidationResults()) {
            if (validationResult is ParameterErrors) {
                validationResult.getFieldErrors().stream()
                    .map<Map<String, String>> { error: FieldError? ->
                        ExceptionHandlerUtil.createValidationError(
                            error!!.getField(),
                            error.getDefaultMessage()
                        )
                    }
                    .forEach { e: Map<String, String> -> errors.add(e) }
            } else {
                val parameterName = validationResult.getMethodParameter().getParameterName()
                validationResult.getResolvableErrors().stream()
                    .map<Map<String, String>> { error: MessageSourceResolvable? ->
                        ExceptionHandlerUtil.createValidationError(
                            parameterName,
                            error!!.getDefaultMessage()
                        )
                    }
                    .forEach { e: Map<String, String> -> errors.add(e) }
            }
        }
        problem.setProperty("errors", errors)

        return problem
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, handleMethodArgumentNotValidException(ex), headers, status, request)
    }

    override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleExceptionInternal(ex, handleHandlerMethodValidationException(ex), headers, status, request)
    }
}
