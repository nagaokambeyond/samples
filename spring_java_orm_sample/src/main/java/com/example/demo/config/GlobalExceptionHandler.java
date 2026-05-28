package com.example.demo.config;

import com.example.demo.exception.RepositoryDataNotfoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
}
