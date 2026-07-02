package com.example.demo.exception;

public class UniqueConstraintValidationException extends RuntimeException {
    public UniqueConstraintValidationException(String tableName, String columnName, String value) {
        super("一意制約に違反しています: " + tableName + "(" + columnName + "=" + value + ")");
    }
}
