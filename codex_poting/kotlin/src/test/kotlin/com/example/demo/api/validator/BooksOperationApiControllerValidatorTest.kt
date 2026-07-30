package com.example.demo.api.validator

import com.example.demo.exception.CorrelationValidationFailureException
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class BooksOperationApiControllerValidatorTest {
    private val validator = BooksOperationApiControllerValidator()

    @Test
    fun searchValidationAllowsBothReleaseDatesUnset() {
        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { validator.searchValidation(null, null) })
    }

    @Test
    fun searchValidationAllowsValidReleaseDateRange() {
        Assertions.assertThatNoException().isThrownBy(ThrowingCallable {
            validator.searchValidation(
                LocalDate.of(2020, 1, 1),
                LocalDate.of(2020, 1, 1)
            )
        }
        )
    }

    @Test
    fun searchValidationThrowsWhenOnlyReleaseDateFromIsSet() {
        Assertions.assertThatThrownBy(ThrowingCallable { validator.searchValidation(LocalDate.of(2020, 1, 1), null) })
            .isInstanceOf(CorrelationValidationFailureException::class.java)
    }

    @Test
    fun searchValidationThrowsWhenReleaseDateFromIsAfterReleaseDateTo() {
        Assertions.assertThatThrownBy(ThrowingCallable {
            validator.searchValidation(
                LocalDate.of(2020, 1, 2),
                LocalDate.of(2020, 1, 1)
            )
        }
        ).isInstanceOf(CorrelationValidationFailureException::class.java)
    }
}
