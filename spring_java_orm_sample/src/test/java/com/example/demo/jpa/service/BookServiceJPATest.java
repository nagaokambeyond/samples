package com.example.demo.jpa.service;

import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import jakarta.persistence.EntityManager;
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
class BookServiceJPATest {
    @Autowired
    private BookServiceJPA bookService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllReturnsBooksOrderedById() {
        final var books = bookService.findAll();

        assertThat(books).hasSizeGreaterThanOrEqualTo(2);
        assertThat(books).extracting("id").containsExactly(1L, 2L);
    }

    @Test
    void findByIdReturnsBook() {
        final var book = bookService.findById(1L);

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
        final var book = bookService.create(new BookCreateRequest("JPA入門", "Jiro", releaseDate, 2L));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("JPA入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(book.getPublisherId()).isEqualTo(2L);
        assertThat(book.getVersion()).isEqualTo(1L);
    }

    @Test
    void createThrowsWhenPublisherDoesNotExist() {
        assertThatThrownBy(() -> bookService.create(new BookCreateRequest("JPA入門", "Jiro", LocalDate.of(2021, 1, 1), 999L)))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class);
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        final var before = bookService.findById(1L);
        final var releaseDate = LocalDate.of(2021, 2, 1);

        final var updated = bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", releaseDate, 2L, before.getVersion()));
        entityManager.flush();
        entityManager.clear();
        final var saved = bookService.findById(1L);

        assertThat(updated.getTitle()).isEqualTo("JPA更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getPublisherId()).isEqualTo(2L);
        assertThat(saved.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(saved.getPublisherId()).isEqualTo(2L);
        assertThat(saved.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(saved.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenPublisherDoesNotExist() {
        final var before = bookService.findById(1L);

        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 999L, before.getVersion())))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class);
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void deleteRemovesBook() {
        bookService.delete(1L);

        assertThatThrownBy(() -> bookService.findById(1L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }
}
