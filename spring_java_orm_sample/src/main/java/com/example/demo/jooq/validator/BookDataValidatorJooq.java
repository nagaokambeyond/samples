package com.example.demo.jooq.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.jooq.entity.BookWithStockRow;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.example.demo.jooq.generated.Tables.BOOK_GENRE;
import static com.example.demo.jooq.generated.Tables.PUBLISHER;

@Component
@RequiredArgsConstructor
public class BookDataValidatorJooq {
    private final DSLContext dsl;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        if (!dsl.fetchExists(PUBLISHER, PUBLISHER.ID.eq(publisherId))) {
            throw new ForeignKeyReferenceNotFoundException("publisher", publisherId);
        }

        if (!dsl.fetchExists(BOOK_GENRE, BOOK_GENRE.ID.eq(genreId))) {
            throw new ForeignKeyReferenceNotFoundException("book_genre", genreId);
        }
    }

    public void versionValidate(Long bookId, Long currentVersion, Long requestVersion) {
        if (!Objects.equals(currentVersion, requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookWithStockRow.class, bookId);
        }
    }
}
