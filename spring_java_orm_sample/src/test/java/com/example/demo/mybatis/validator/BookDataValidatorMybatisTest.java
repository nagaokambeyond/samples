package com.example.demo.mybatis.validator;

import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.mybatis.generator.entity.BookEntity;
import com.example.demo.mybatis.generator.entity.BookEntityExample;
import com.example.demo.mybatis.generator.entity.BookGenreEntity;
import com.example.demo.mybatis.generator.entity.PublisherEntity;
import com.example.demo.mybatis.generator.mapper.BookGenreMapper;
import com.example.demo.mybatis.generator.mapper.BookMapper;
import com.example.demo.mybatis.generator.mapper.PublisherMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class BookDataValidatorMybatisTest {
    private BookMapper bookMapper;
    private PublisherMapper publisherMapper;
    private BookGenreMapper bookGenreMapper;
    private BookDataValidatorMybatis validator;

    @BeforeEach
    void setUp() {
        bookMapper = mock(BookMapper.class);
        publisherMapper = mock(PublisherMapper.class);
        bookGenreMapper = mock(BookGenreMapper.class);
        validator = new BookDataValidatorMybatis(bookMapper, publisherMapper, bookGenreMapper);
    }

    @Test
    void foreignKeyValidateAllowsExistingPublisherAndGenre() {
        when(publisherMapper.selectByPrimaryKey(1L)).thenReturn(new PublisherEntity());
        when(bookGenreMapper.selectByPrimaryKey(5L)).thenReturn(new BookGenreEntity());

        assertThatNoException().isThrownBy(() -> validator.foreignKeyValidate(1L, 5L));
    }

    @Test
    void foreignKeyValidateThrowsWhenPublisherDoesNotExist() {
        when(publisherMapper.selectByPrimaryKey(999L)).thenReturn(null);

        assertThatThrownBy(() -> validator.foreignKeyValidate(999L, 5L))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
        verifyNoInteractions(bookGenreMapper);
    }

    @Test
    void foreignKeyValidateThrowsWhenBookGenreDoesNotExist() {
        when(publisherMapper.selectByPrimaryKey(1L)).thenReturn(new PublisherEntity());
        when(bookGenreMapper.selectByPrimaryKey(999L)).thenReturn(null);

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
                .isEqualTo(BookEntity.class));
    }

    @Test
    void uniqueIsbnValidateAllowsUnusedIsbn() {
        when(bookMapper.selectByExample(any(BookEntityExample.class))).thenReturn(List.of());

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateAllowsKeepingCurrentBookIsbn() {
        when(bookMapper.selectByExample(any(BookEntityExample.class))).thenReturn(List.of(book(1L, 2L)));

        assertThatNoException().isThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L));
    }

    @Test
    void uniqueIsbnValidateThrowsWhenIsbnBelongsToAnotherBook() {
        when(bookMapper.selectByExample(any(BookEntityExample.class))).thenReturn(List.of(book(2L, 1L)));

        assertThatThrownBy(() -> validator.uniqueIsbnValidate("9784000000001", 1L))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=9784000000001)");
    }

    private BookEntity book(Long id, Long version) {
        final var book = new BookEntity();
        book.setId(id);
        book.setVersion(version);
        return book;
    }
}
