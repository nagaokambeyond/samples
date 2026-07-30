package com.example.demo.api.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [])
@Size(min = 13, max = 13)
@Pattern(regexp = "\\d{13}")
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.ANNOTATION_CLASS
)
@Retention(AnnotationRetention.RUNTIME)
annotation class Isbn(
    val message: String = "ISBN must be 13 digits",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
