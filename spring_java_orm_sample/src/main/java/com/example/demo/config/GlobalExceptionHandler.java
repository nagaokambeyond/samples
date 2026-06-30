package com.example.demo.config;

import com.example.demo.exception.CorrelationValidationFailureException;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.LoginRateLimitExceededException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@NullMarked
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(RepositoryDataNotfoundException.class)
    public ProblemDetail handleRepositoryDataNotfoundException(RepositoryDataNotfoundException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("該当データなし");

        return problem;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
        return createConflictProblem();
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ProblemDetail handlePessimisticLockingFailureException(PessimisticLockingFailureException ex) {
        return createConflictProblem();
    }

    private ProblemDetail createConflictProblem() {
        final var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);

        problem.setTitle("更新競合");
        problem.setDetail("他ユーザーによって更新されています");

        return problem;
    }

    @ExceptionHandler(CorrelationValidationFailureException.class)
    public ProblemDetail handleCorrelationValidationFailureException(CorrelationValidationFailureException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("相関バリデーション");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(ForeignKeyReferenceNotFoundException.class)
    public ProblemDetail handleForeignKeyReferenceNotFoundException(ForeignKeyReferenceNotFoundException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("データバリデーション");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problem.setTitle("認証エラー");
        problem.setDetail("ユーザー名またはパスワードが不正です");

        return problem;
    }

    @ExceptionHandler(LoginRateLimitExceededException.class)
    public ProblemDetail handleLoginRateLimitExceededException(LoginRateLimitExceededException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.TOO_MANY_REQUESTS);

        problem.setTitle("リクエスト回数制限");
        problem.setDetail("ログインリクエスト回数が日次上限を超えました");

        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("リクエストエラー");
        problem.setDetail(ex.getMessage());
        problem.setProperty(
            "errors",
            ex.getConstraintViolations().stream()
                .map(violation -> createValidationError(
                    getLastPropertyName(violation.getPropertyPath()),
                    violation.getMessage()
                ))
                .toList()
        );

        return problem;
    }

    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("リクエストバリデーションエラー");
        problem.setProperty(
            "errors",
            ex.getBindingResult().getFieldErrors().stream()
                .map(error -> createValidationError(error.getField(), error.getDefaultMessage()))
                .toList()
        );

        return problem;
    }

    public ProblemDetail handleHandlerMethodValidationException(HandlerMethodValidationException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        final var errors = new ArrayList<Map<String, String>>();

        problem.setTitle("リクエストバリデーションエラー");
        for (final var validationResult : ex.getParameterValidationResults()) {
            if (validationResult instanceof ParameterErrors parameterErrors) {
                parameterErrors.getFieldErrors().stream()
                    .map(error -> createValidationError(error.getField(), error.getDefaultMessage()))
                    .forEach(errors::add);
            } else {
                final var parameterName = validationResult.getMethodParameter().getParameterName();
                validationResult.getResolvableErrors().stream()
                    .map(error -> createValidationError(parameterName, error.getDefaultMessage()))
                    .forEach(errors::add);
            }
        }
        problem.setProperty("errors", errors);

        return problem;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return handleExceptionInternal(ex, handleMethodArgumentNotValidException(ex), headers, status, request);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request
    ) {
        return handleExceptionInternal(ex, handleHandlerMethodValidationException(ex), headers, status, request);
    }

    private Map<String, String> createValidationError(@Nullable String field, @Nullable String message) {
        return Map.of(
            "field", Objects.toString(field, ""),
            "message", Objects.toString(message, "")
        );
    }

    private String getLastPropertyName(Path propertyPath) {
        String propertyName = "";
        for (final var node : propertyPath) {
            if (node.getName() != null) {
                propertyName = node.getName();
            }
        }
        return propertyName;
    }

}
