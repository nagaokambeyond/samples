package com.example.demo.api.validator;

import com.example.demo.exception.CorrelationValidationFailureException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Component
public class BooksOperationApiControllerValidator {
    public void searchValidation(
        LocalDate releaseDateFrom,
        LocalDate releaseDateTo
    ) {
        if (Objects.isNull(releaseDateFrom) && Objects.isNull(releaseDateTo)) {
            return;
        }

        if (Objects.nonNull(releaseDateFrom) && Objects.nonNull(releaseDateTo)) {
            if (releaseDateFrom.isAfter(releaseDateTo)) {
                throw new CorrelationValidationFailureException("発売日付From＜＝発売日付Toにしてください。");
            }
        } else {
            throw new CorrelationValidationFailureException("発売日付From、発売日付To両方設定してください。");
        }
    }
}
