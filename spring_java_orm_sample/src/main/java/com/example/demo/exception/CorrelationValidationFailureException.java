package com.example.demo.exception;

public class CorrelationValidationFailureException extends RuntimeException {
    public CorrelationValidationFailureException(String message) {
        super(message);
    }
}
