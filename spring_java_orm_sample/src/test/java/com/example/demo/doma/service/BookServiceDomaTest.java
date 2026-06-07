package com.example.demo.doma.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookServiceDomaTest {
    @Autowired
    private BookService bookService;

    @Test
    void usesDomaAsPrimaryBookService() {
        assertThat(bookService).isInstanceOf(BookServiceDoma.class);
    }

    @Test
    void findAllReturnsBooksOrderedById() {
        var books = bookService.findAll();

        assertThat(books).hasSizeGreaterThanOrEqualTo(2);
        assertThat(books).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void findByIdReturnsBook() {
        var book = bookService.findById(1L);

        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Spring入門");
        assertThat(book.getPublisherId()).isEqualTo(1L);
    }

    @Test
    void findByIdThrowsWhenBookDoesNotExist() {
        assertThatThrownBy(() -> bookService.findById(999L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void searchIgnoresCase() {
        final var books = bookService.search("spring", null, null);

        assertThat(books).extracting("title").containsExactly("Spring入門");
    }

    @Test
    void searchFiltersByReleaseDateRange() {
        final var books = bookService.search("spring", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1));

        assertThat(books).extracting("title").containsExactly("Spring入門");
    }

    @Test
    void createReturnsGeneratedIdAndResponse() {
        final var releaseDate = LocalDate.of(2021, 1, 1);
        final var book = bookService.create(new BookCreateRequest("Doma入門", "Jiro", releaseDate, 2L));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Doma入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(book.getPublisherId()).isEqualTo(2L);
        assertThat(book.getVersion()).isEqualTo(1L);
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        var before = bookService.findById(1L);
        final var releaseDate = LocalDate.of(2021, 2, 1);

        final var updated = bookService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", releaseDate, 2L, before.getVersion()));

        assertThat(updated.getTitle()).isEqualTo("Doma更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(updated.getPublisherId()).isEqualTo(2L);
        assertThat(updated.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(updated.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void deleteRemovesBook() {
        bookService.delete(1L);

        assertThatThrownBy(() -> bookService.findById(1L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }
}
