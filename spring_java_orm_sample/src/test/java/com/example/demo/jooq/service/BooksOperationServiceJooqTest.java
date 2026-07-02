package com.example.demo.jooq.service;

import com.example.demo.BookRowLock;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.service.BooksOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("jooq")
@Transactional
class BooksOperationServiceJooqTest {
    @Autowired
    private BooksOperationService primaryBooksOperationService;

    @Autowired
    private BooksOperationServiceJooq booksOperationService;

    @Autowired
    private DataSource dataSource;

    @Test
    void usesJooqAsPrimaryBookService() {
        assertThat(primaryBooksOperationService).isInstanceOf(BooksOperationServiceJooq.class);
    }

    @Test
    void findByIdReturnsBook() {
        final var book = booksOperationService.findById(1L);

        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Spring入門");
        assertThat(book.getPublisherId()).isEqualTo(1L);
        assertThat(book.getPublisherName()).isEqualTo("◯◯書房");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getIsbn()).isEqualTo("0000000000001");
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
        assertThatThrownBy(() -> booksOperationService.findById(999L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void searchIgnoresCaseAndMatchesStartingKeyword() {
        final var books = booksOperationService.search("spring", null, null, 0, 10);

        assertThat(books.getContent()).extracting("title").contains("Spring入門");
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchDoesNotMatchContainingKeyword() {
        final var books = booksOperationService.search("入門", null, null, 0, 10);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getTotalElements()).isZero();
        assertThat(books.getTotalPages()).isZero();
    }

    @Test
    void searchDoesNotFilterByBlankKeywordAndAggregatesStock() {
        final var books = booksOperationService.search("   ", null, null, 0, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(1L, 2L);
        assertThat(books.getContent().getFirst().getBookStockList())
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                tuple(1L, 1L, "あ駅前店", 10),
                tuple(2L, 2L, "い駅前店", 20),
                tuple(3L, 3L, "う駅前店", 30)
            );
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(21);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchAppliesReleaseDateRangeWithPaging() {
        final var books = booksOperationService.search("はじめて", LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1), 1, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(4L, 5L);
        assertThat(books.getPage()).isEqualTo(1);
        assertThat(books.getSize()).isEqualTo(2);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(20);
    }

    @Test
    void createReturnsGeneratedIdAndResponse() {
        final var releaseDate = LocalDate.of(2021, 1, 1);
        final var book = booksOperationService.create(new BookCreateRequest("jOOQ入門", "Jiro", releaseDate, 2L, 5L, "9784000000401"));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("jOOQ入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(book.getPublisherId()).isEqualTo(2L);
        assertThat(book.getPublisherName()).isEqualTo("△△出版");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getIsbn()).isEqualTo("9784000000401");
        assertThat(book.getVersion()).isEqualTo(1L);
    }

    @Test
    void createThrowsWhenPublisherDoesNotExist() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("jOOQ入門", "Jiro", LocalDate.of(2021, 1, 1), 999L, 5L, "9784000000402")))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void createThrowsWhenBookGenreDoesNotExist() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("jOOQ入門", "Jiro", LocalDate.of(2021, 1, 1), 1L, 999L, "9784000000403")))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void createThrowsWhenIsbnAlreadyExists() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("jOOQ入門", "Jiro", LocalDate.of(2021, 1, 1), 1L, 5L, "0000000000001")))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000001)");
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        final var before = booksOperationService.findById(1L);
        final var releaseDate = LocalDate.of(2021, 2, 1);

        final var updated = booksOperationService.update(new BookUpdateRequest(1L, "jOOQ更新", "Saburo", releaseDate, 2L, 5L, "9784000000411", before.getVersion()));

        assertThat(updated.getTitle()).isEqualTo("jOOQ更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(updated.getIsbn()).isEqualTo("9784000000411");
        assertThat(updated.getPublisherId()).isEqualTo(2L);
        assertThat(updated.getPublisherName()).isEqualTo("△△出版");
        assertThat(updated.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(updated.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "jOOQ更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "9784000000412", -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void updateThrowsWhenIsbnAlreadyExistsInAnotherBook() {
        final var before = booksOperationService.findById(1L);

        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "jOOQ更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "0000000000002", before.getVersion())))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000002)");
    }

    @Test
    void updateAllowsKeepingCurrentIsbn() {
        final var before = booksOperationService.findById(1L);

        final var updated = booksOperationService.update(new BookUpdateRequest(1L, "jOOQ更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, before.getIsbn(), before.getVersion()));

        assertThat(updated.getIsbn()).isEqualTo(before.getIsbn());
    }

    @Test
    void updateThrowsWhenWriteLockCannotBeAcquired() throws Exception {
        final var before = booksOperationService.findById(1L);

        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "jOOQ更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "9784000000413", before.getVersion())))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }
    }

    @Test
    void deleteRemovesBook() {
        booksOperationService.delete(1L);

        assertThatThrownBy(() -> booksOperationService.findById(1L))
            .isInstanceOf(RepositoryDataNotfoundException.class);
    }

    @Test
    void deleteThrowsWhenWriteLockCannotBeAcquired() throws Exception {
        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> booksOperationService.delete(1L))
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
