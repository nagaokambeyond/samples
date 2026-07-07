package com.example.demo.config;

import com.example.demo.api.BooksOperationApi;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.LoginRateLimitExceededException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.jpa.entity.Publisher;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.constraints.Min;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePessimisticLockingFailureExceptionReturnsConflict() {
        final var problem = handler.handlePessimisticLockingFailureException(new CannotAcquireLockException("lock failed"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problem.getTitle()).isEqualTo("更新競合");
        assertThat(problem.getDetail()).isEqualTo("他ユーザーによって更新されています");
    }

    @Test
    void handleForeignKeyReferenceNotFoundExceptionReturnsMissingReferences() {
        final var ex = new ForeignKeyReferenceNotFoundException(Publisher.class, 999L);

        final var problem = handler.handleForeignKeyReferenceNotFoundException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("データバリデーション");
        assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void handleForeignKeyReferenceNotFoundExceptionRemovesEntitySuffix() {
        final var ex = new ForeignKeyReferenceNotFoundException(BookEntity.class, 999L);

        final var problem = handler.handleForeignKeyReferenceNotFoundException(ex);

        assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: book(id=999)");
    }

    @Test
    void handleForeignKeyReferenceNotFoundExceptionConvertsEntityNameToSnakeCase() {
        final var ex = new ForeignKeyReferenceNotFoundException(BookGenreEntity.class, 999L);

        final var problem = handler.handleForeignKeyReferenceNotFoundException(ex);

        assertThat(problem.getDetail()).isEqualTo("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void handleUniqueConstraintValidationExceptionReturnsBadRequest() {
        final var ex = new UniqueConstraintValidationException("book", "isbn", "0000000000001");

        final var problem = handler.handleUniqueConstraintValidationException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("データバリデーション");
        assertThat(problem.getDetail()).isEqualTo("一意制約に違反しています: book(isbn=0000000000001)");
    }

    @Test
    void handleMethodArgumentNotValidExceptionReturnsFieldErrors() throws Exception {
        final var request = new BookCreateRequest("", null, null, null, null, null, null);
        final var bindingResult = new BeanPropertyBindingResult(request, "request");
        bindingResult.addError(new FieldError("request", "title", "size must be between 1 and 100"));
        bindingResult.addError(new FieldError("request", "releaseDate", "must not be null"));
        bindingResult.addError(new FieldError("request", "isbn", "must not be null"));
        final var methodParameter = new MethodParameter(
            BooksOperationApi.class.getDeclaredMethod("createBook", BookCreateRequest.class),
            0
        );
        final var ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        final var problem = handler.handleMethodArgumentNotValidException(ex);

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problem.getTitle()).isEqualTo("リクエストバリデーションエラー");
        assertThat(getErrorFields(problem.getProperties())).containsExactly("title", "releaseDate", "isbn");
        assertThat(getErrorMessages(problem.getProperties()))
            .containsExactly("size must be between 1 and 100", "must not be null", "must not be null");
    }

    @Test
    void handleConstraintViolationExceptionReturnsFieldErrors() {
        try (final var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final var validator = validatorFactory.getValidator();
            final var violations = validator.validate(new SearchCondition(-1));
            final var ex = new ConstraintViolationException(violations);

            final var problem = handler.handleConstraintViolationException(ex);

            assertThat(problem.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            assertThat(problem.getTitle()).isEqualTo("リクエストエラー");
            assertThat(problem.getDetail()).isNotBlank();
            assertThat(getErrorFields(problem.getProperties())).containsExactly("page");
            assertThat(getErrorMessages(problem.getProperties()).getFirst()).isNotBlank();
        }
    }

    @Test
    void handleLoginRateLimitExceededExceptionReturnsTooManyRequests() {
        final var problem = handler.handleLoginRateLimitExceededException(new LoginRateLimitExceededException());

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(problem.getTitle()).isEqualTo("リクエスト回数制限");
        assertThat(problem.getDetail()).isEqualTo("ログインリクエスト回数が日次上限を超えました");
    }

    private List<String> getErrorFields(Map<String, Object> properties) {
        return getErrors(properties).stream()
            .map(error -> error.get("field"))
            .toList();
    }

    private List<String> getErrorMessages(Map<String, Object> properties) {
        return getErrors(properties).stream()
            .map(error -> error.get("message"))
            .toList();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> getErrors(Map<String, Object> properties) {
        return (List<Map<String, String>>) properties.get("errors");
    }

    private record SearchCondition(@Min(0) int page) {
    }
}
