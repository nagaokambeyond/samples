package com.example.demo.util

import jakarta.validation.Path
import java.util.*

object ExceptionHandlerUtil {
    fun createValidationError(field: String?, message: String?): Map<String, String> {
        return mapOf(
            "field" to Objects.toString(field, ""),
            "message" to Objects.toString(message, "")
        )
    }

    fun getLastPropertyName(propertyPath: Path): String? {
        var propertyName: String? = ""
        for (node in propertyPath) {
            if (node.getName() != null) {
                propertyName = node.getName()
            }
        }
        return propertyName
    }
}
