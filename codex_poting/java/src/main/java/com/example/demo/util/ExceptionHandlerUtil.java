package com.example.demo.util;

import jakarta.validation.Path;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class ExceptionHandlerUtil {
    public static Map<String, String> createValidationError(@Nullable String field, @Nullable String message) {
        return Map.of(
            "field", Objects.toString(field, ""),
            "message", Objects.toString(message, "")
        );
    }

    public static String getLastPropertyName(Path propertyPath) {
        String propertyName = "";
        for (final var node : propertyPath) {
            if (node.getName() != null) {
                propertyName = node.getName();
            }
        }
        return propertyName;
    }
}
