package com.example.demo.api.validator;

import com.example.demo.exception.CorrelationValidationFailureException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookApiControllerValidatorTest {
    private final BookApiControllerValidator validator = new BookApiControllerValidator();

    @Test
    void searchValidationAllowsBothReleaseDatesUnset() {
        assertThatNoException().isThrownBy(() -> validator.searchValidation(null, null));
    }

    @Test
    void searchValidationAllowsValidReleaseDateRange() {
        assertThatNoException().isThrownBy(() ->
            validator.searchValidation(LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1))
        );
    }

    @Test
    void searchValidationThrowsWhenOnlyReleaseDateFromIsSet() {
        assertThatThrownBy(() -> validator.searchValidation(LocalDate.of(2020, 1, 1), null))
            .isInstanceOf(CorrelationValidationFailureException.class);
    }

    @Test
    void searchValidationThrowsWhenReleaseDateFromIsAfterReleaseDateTo() {
        assertThatThrownBy(() ->
            validator.searchValidation(LocalDate.of(2020, 1, 2), LocalDate.of(2020, 1, 1))
        ).isInstanceOf(CorrelationValidationFailureException.class);
    }
}
