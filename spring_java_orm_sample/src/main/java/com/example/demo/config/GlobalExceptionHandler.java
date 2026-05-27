package com.example.demo.config;

import com.example.demo.exception.RepositoryDataNotfoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleException(HandlerMethodValidationException ex) {

//        List<String> details = ex.getBindingResult()
//            .getFieldErrors()
//            .stream()
//            .map(error -> error.getField() + ": " + error.getDefaultMessage())
//            .toList();

        return new ResponseEntity<>(
            ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, "").build(),
            HttpStatus.BAD_REQUEST
        );
    }

//    @ExceptionHandler(HandlerMethodValidationException.class)
//    public ResponseEntity<String> handleMethodValidationException(HandlerMethodValidationException e) {
//        // シンプルな文字列を返す
//        return new ResponseEntity<>("パラメータが範囲外です: " + e.getMessage(), HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> details = ex.getConstraintViolations()
            .stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .toList();

        return new ResponseEntity<>(
            ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, details.toString()).build(),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RepositoryDataNotfoundException.class)
    public ProblemDetail handleRepositoryDataNotfoundException(RepositoryDataNotfoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);

        problem.setTitle("Update data not found");
        problem.setDetail(ex.getMessage());

        return problem;
    }
}
