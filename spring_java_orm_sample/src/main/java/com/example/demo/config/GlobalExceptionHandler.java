package com.example.demo.config;

import com.example.demo.exception.CorrelationValidationFailureException;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RepositoryDataNotfoundException.class)
    public ProblemDetail handleRepositoryDataNotfoundException(RepositoryDataNotfoundException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("該当データなし");
        problem.setDetail(ex.getMessage());

        return problem;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleObjectOptimisticLockingFailureException(ObjectOptimisticLockingFailureException ex) {
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        final var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        problem.setTitle("リクエストエラー");
        problem.setDetail(ex.getMessage());

        return problem;
    }

}
