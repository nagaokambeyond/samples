package com.example.demo.jooq.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.jooq.dsl.BookDsl;
import com.example.demo.jooq.dsl.BookGenreDsl;
import com.example.demo.jooq.dsl.PublisherDsl;
import com.example.demo.jooq.entity.BookWithStockRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookDataValidatorJooqTest {
    private BookDsl bookDsl;
    private PublisherDsl publisherDsl;
    private BookGenreDsl bookGenreDsl;
    private BookDataValidatorJooq validator;

    @BeforeEach
    void setUp() {
        bookDsl = mock(BookDsl.class);
        publisherDsl = mock(PublisherDsl.class);
        bookGenreDsl = mock(BookGenreDsl.class);
        validator = new BookDataValidatorJooq(bookDsl, publisherDsl, bookGenreDsl);
    }

    @Test
    void foreignKeyValidateAllowsExistingPublisherAndGenre() {
        when(publisherDsl.existsPublisher(1L)).thenReturn(true);
        when(bookGenreDsl.exists(5L)).thenReturn(true);

        assertThatNoException().isThrownBy(() -> validator.foreignKeyValidate(1L, 5L));
    }

    @Test
    void foreignKeyValidateThrowsWhenPublisherDoesNotExist() {
        when(publisherDsl.existsPublisher(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(999L, 5L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
        verifyNoInteractions(bookGenreDsl);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookGenreDoesNotExist() {
        when(publisherDsl.existsPublisher(1L)).thenReturn(true);
        when(bookGenreDsl.exists(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(1L, 999L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void versionValidateAllowsMatchingVersion() {
        assertThatNoException().isThrownBy(() -> validator.versionValidate(1L, 2L, 2L));
    }

    @Test
    void versionValidateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> validator.versionValidate(1L, 2L, 1L))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class)
            .satisfies(exception -> assertThat(((ObjectOptimisticLockingFailureException) exception).getPersistentClass())
                .isEqualTo(BookWithStockRow.class));
    }

    @Test
    void uniqueIsbnValidateAllowsUnusedIsbn() {
        when(bookDsl.selectIdByIsbn("9784000000001")).thenReturn(null);

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateAllowsKeepingCurrentBookIsbn() {
        when(bookDsl.selectIdByIsbn("9784000000001")).thenReturn(1L);

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateThrowsWhenIsbnBelongsToAnotherBook() {
        when(bookDsl.selectIdByIsbn("9784000000001")).thenReturn(2L);

        assertThatThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=9784000000001)");
    }
}
