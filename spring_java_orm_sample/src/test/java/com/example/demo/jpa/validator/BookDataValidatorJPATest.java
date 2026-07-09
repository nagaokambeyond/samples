package com.example.demo.jpa.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.jpa.entity.Book;
import com.example.demo.jpa.repository.BookGenreRepository;
import com.example.demo.jpa.repository.BookRepository;
import com.example.demo.jpa.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookDataValidatorJPATest {
    private BookRepository bookRepository;
    private PublisherRepository publisherRepository;
    private BookGenreRepository bookGenreRepository;
    private BookDataValidatorJPA validator;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        publisherRepository = mock(PublisherRepository.class);
        bookGenreRepository = mock(BookGenreRepository.class);
        validator = new BookDataValidatorJPA(bookRepository, publisherRepository, bookGenreRepository);
    }

    @Test
    void foreignKeyValidateAllowsExistingPublisherAndGenre() {
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(bookGenreRepository.existsById(5L)).thenReturn(true);

        assertThatNoException().isThrownBy(() -> validator.foreignKeyValidate(1L, 5L));
    }

    @Test
    void foreignKeyValidateThrowsWhenPublisherDoesNotExist() {
        when(publisherRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(999L, 5L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
        verifyNoInteractions(bookGenreRepository);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookGenreDoesNotExist() {
        when(publisherRepository.existsById(1L)).thenReturn(true);
        when(bookGenreRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> validator.foreignKeyValidate(1L, 999L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void versionValidateAllowsMatchingVersion() {
        final var book = book(1L, 2L);

        assertThatNoException().isThrownBy(() -> validator.versionValidate(book, 2L));
    }

    @Test
    void versionValidateThrowsWhenVersionIsStale() {
        final var book = book(1L, 2L);

        assertThatThrownBy(() -> validator.versionValidate(book, 1L))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class)
            .satisfies(exception -> assertThat(((ObjectOptimisticLockingFailureException) exception).getPersistentClass())
                .isEqualTo(Book.class));
    }

    @Test
    void uniqueIsbnValidateAllowsUnusedIsbn() {
        when(bookRepository.findByIsbn("9784000000001")).thenReturn(Optional.empty());

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateAllowsKeepingCurrentBookIsbn() {
        when(bookRepository.findByIsbn("9784000000001")).thenReturn(Optional.of(book(1L, 2L)));

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateThrowsWhenIsbnBelongsToAnotherBook() {
        when(bookRepository.findByIsbn("9784000000001")).thenReturn(Optional.of(book(2L, 1L)));

        assertThatThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=9784000000001)");
    }

    private Book book(Long id, Long version) {
        final var book = new Book();
        book.setId(id);
        book.setVersion(version);
        return book;
    }
}
