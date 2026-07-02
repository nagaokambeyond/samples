package com.example.demo.jooq.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.jooq.dsl.BookDsl;
import com.example.demo.jooq.dsl.BookGenreDsl;
import com.example.demo.jooq.dsl.PublisherDsl;
import com.example.demo.jooq.entity.BookWithStockRow;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class BookDataValidatorJooq {
    private final BookDsl bookDsl;
    private final PublisherDsl publisherDsl;
    private final BookGenreDsl bookGenreDsl;

    public void foreignKeyValidate(Long publisherId, Long genreId) {
        if (!publisherDsl.existsPublisher(publisherId)) {
            throw new ForeignKeyReferenceNotFoundException("publisher", publisherId);
        }

        if (!bookGenreDsl.exists(genreId)) {
            throw new ForeignKeyReferenceNotFoundException("book_genre", genreId);
        }
    }

    public void versionValidate(Long bookId, Long currentVersion, Long requestVersion) {
        if (!Objects.equals(currentVersion, requestVersion)) {
            throw new ObjectOptimisticLockingFailureException(BookWithStockRow.class, bookId);
        }
    }

    public void uniqueIsbnValidate(String isbn, Long bookId) {
        final var existingBookId = bookDsl.selectIdByIsbn(isbn);
        if (Objects.nonNull(existingBookId) && !Objects.equals(existingBookId, bookId)) {
            throw new UniqueConstraintValidationException("book", "isbn", isbn);
        }
    }
}
