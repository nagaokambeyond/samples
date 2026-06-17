package com.example.demo.jpa.service;

import com.example.demo.BookRowLock;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class BooksOperationServiceJPATest {
    @Autowired
    private BooksOperationServiceJPA bookService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @Test
    void findByIdReturnsBook() {
        final var book = bookService.findById(1L);

        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Spring入門");
        assertThat(book.getPublisherId()).isEqualTo(1L);
        assertThat(book.getPublisherName()).isEqualTo("◯◯書房");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getBookStockList())
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                tuple(1L, 1L, "あ駅前店", 10),
                tuple(2L, 2L, "い駅前店", 20),
                tuple(3L, 3L, "う駅前店", 30)
            );
    }

    @Test
    void findByIdThrowsWhenBookDoesNotExist() {
        assertThatThrownBy(() -> bookService.findById(999L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void searchIgnoresCase() {
        final var books = bookService.search("spring", null, null, 0, 10);

        assertThat(books.getContent()).extracting("title").contains("Spring入門");
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchMatchesAuthorStartingWithIgnoreCase() {
        final var books = bookService.search("taro", null, null, 0, 10);

        assertThat(books.getContent()).extracting("id").containsExactly(1L);
        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchDoesNotMatchTitleContainingKeyword() {
        final var books = bookService.search("入門", null, null, 0, 10);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getTotalElements()).isZero();
        assertThat(books.getTotalPages()).isZero();
    }

    @Test
    void searchDoesNotMatchAuthorContainingKeyword() {
        final var books = bookService.search("aro", null, null, 0, 10);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getTotalElements()).isZero();
        assertThat(books.getTotalPages()).isZero();
    }

    @Test
    void searchFiltersByReleaseDateRange() {
        final var books = bookService.search("spring", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1), 0, 10);

        assertThat(books.getContent()).extracting("title").contains("Spring入門");
    }

    @Test
    void searchDoesNotFilterByKeywordWhenKeywordIsNull() {
        final var books = bookService.search(null, null, null, 0, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(1L, 2L);
        assertThat(books.getContent().getFirst().getBookStockList())
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                tuple(1L, 1L, "あ駅前店", 10),
                tuple(2L, 2L, "い駅前店", 20),
                tuple(3L, 3L, "う駅前店", 30)
            );
        assertThat(books.getContent().get(1).getBookStockList())
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                tuple(4L, 1L, "あ駅前店", 11),
                tuple(5L, 2L, "い駅前店", 21),
                tuple(6L, 3L, "う駅前店", 31)
            );
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(21);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchDoesNotFilterByKeywordWhenKeywordIsBlank() {
        final var books = bookService.search("   ", null, null, 0, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(1L, 2L);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(21);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchReturnsFirstPageAndMetadata() {
        final var books = bookService.search("はじめて", null, null, 0, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(2L, 3L);
        assertThat(books.getContent()).extracting("publisherName").containsOnly("△△出版");
        assertThat(books.getContent()).extracting("genreId").containsOnly(5L);
        assertThat(books.getContent()).extracting("genreName").containsOnly("工学");
        assertThat(books.getPage()).isEqualTo(0);
        assertThat(books.getSize()).isEqualTo(2);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(20);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchReturnsSecondPage() {
        final var books = bookService.search("はじめて", null, null, 1, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(4L, 5L);
    }

    @Test
    void findByIdReturnsEmptyBookStockListWhenBookHasNoStock() {
        final var book = bookService.findById(6L);

        assertThat(book.getBookStockList()).isEmpty();
    }

    @Test
    void searchReturnsEmptyContentWhenPageOutOfRange() {
        final var firstPage = bookService.search("はじめて", null, null, 0, 2);
        final var books = bookService.search("はじめて", null, null, firstPage.getTotalPages(), 2);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getPage()).isEqualTo(firstPage.getTotalPages());
        assertThat(books.getSize()).isEqualTo(2);
        assertThat(books.getTotalElements()).isEqualTo(firstPage.getTotalElements());
        assertThat(books.getTotalPages()).isEqualTo(firstPage.getTotalPages());
    }

    @Test
    void searchAppliesReleaseDateRangeWithPaging() {
        final var books = bookService.search("はじめて", LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1), 0, 3);

        assertThat(books.getContent()).extracting("id").containsExactly(2L, 3L, 4L);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(20);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void createReturnsGeneratedIdAndResponse() {
        final var releaseDate = LocalDate.of(2021, 1, 1);
        final var book = bookService.create(new BookCreateRequest("JPA入門", "Jiro", releaseDate, 2L, 5L));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("JPA入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(book.getPublisherId()).isEqualTo(2L);
        assertThat(book.getPublisherName()).isEqualTo("△△出版");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getVersion()).isEqualTo(1L);
    }

    @Test
    void createThrowsWhenPublisherDoesNotExist() {
        assertThatThrownBy(() -> bookService.create(new BookCreateRequest("JPA入門", "Jiro", LocalDate.of(2021, 1, 1), 999L, 5L)))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void createThrowsWhenBookGenreDoesNotExist() {
        assertThatThrownBy(() -> bookService.create(new BookCreateRequest("JPA入門", "Jiro", LocalDate.of(2021, 1, 1), 1L, 999L)))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        final var before = bookService.findById(1L);
        final var releaseDate = LocalDate.of(2021, 2, 1);

        final var updated = bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", releaseDate, 2L, 5L, before.getVersion()));
        entityManager.flush();
        entityManager.clear();
        final var saved = bookService.findById(1L);

        assertThat(updated.getTitle()).isEqualTo("JPA更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getPublisherId()).isEqualTo(2L);
        assertThat(updated.getPublisherName()).isEqualTo("△△出版");
        assertThat(updated.getGenreId()).isEqualTo(5L);
        assertThat(updated.getGenreName()).isEqualTo("工学");
        assertThat(saved.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(saved.getPublisherId()).isEqualTo(2L);
        assertThat(saved.getGenreId()).isEqualTo(5L);
        assertThat(saved.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(saved.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenPublisherDoesNotExist() {
        final var before = bookService.findById(1L);

        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 999L, 5L, before.getVersion())))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void updateThrowsWhenBookGenreDoesNotExist() {
        final var before = bookService.findById(1L);

        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 999L, before.getVersion())))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void updateThrowsWhenWriteLockCannotBeAcquired() throws Exception {
        final var before = bookService.findById(1L);

        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> bookService.update(new BookUpdateRequest(1L, "JPA更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, before.getVersion())))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }
    }

    @Test
    void deleteRemovesBook() {
        bookService.delete(1L);

        assertThatThrownBy(() -> bookService.findById(1L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void deleteThrowsWhenWriteLockCannotBeAcquired() throws Exception {
        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> bookService.delete(1L))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }
    }

    private int calculateTotalPages(long totalElements, int size) {
        if (totalElements == 0) {
            return 0;
        }
        return (int) ((totalElements + size - 1) / size);
    }
}
