package com.example.demo.exception;

public class ForeignKeyReferenceNotFoundException extends RuntimeException {
    public ForeignKeyReferenceNotFoundException(String message) {
        super(message);
    }
}
