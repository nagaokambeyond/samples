package com.example.demo.api.validator

import com.example.demo.exception.CorrelationValidationFailureException
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.*

@Component
class BooksOperationApiControllerValidator {
    fun searchValidation(
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?
    ) {
        if (Objects.isNull(releaseDateFrom) && Objects.isNull(releaseDateTo)) {
            return
        }

        if (Objects.nonNull(releaseDateFrom) && Objects.nonNull(releaseDateTo)) {
            if (releaseDateFrom!!.isAfter(releaseDateTo)) {
                throw CorrelationValidationFailureException("発売日付From＜＝発売日付Toにしてください。")
            }
        } else {
            throw CorrelationValidationFailureException("発売日付From、発売日付To両方設定してください。")
        }
    }
}
