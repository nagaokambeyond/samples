package com.example.demo.config;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jpa.entity.Publisher;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.http.HttpStatus;

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
}
