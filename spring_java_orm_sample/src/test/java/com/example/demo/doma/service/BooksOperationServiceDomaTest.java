package com.example.demo.doma.service;

import com.example.demo.BookRowLock;
import com.example.demo.api.request.BookCreateRequest;
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.exception.RepositoryDataNotfoundException;
import com.example.demo.exception.UniqueConstraintValidationException;
import com.example.demo.service.BooksOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("doma")
@Transactional
class BooksOperationServiceDomaTest {
    @Autowired
    private BooksOperationService booksOperationService;

    @Autowired
    private DataSource dataSource;

    @Test
    void usesDomaAsPrimaryBookService() {
        assertThat(booksOperationService).isInstanceOf(BooksOperationServiceDoma.class);
    }

    @Test
    void findByIdReturnsBook() {
        var book = booksOperationService.findById(1L);

        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("Spring入門");
        assertThat(book.getPublisherId()).isEqualTo(1L);
        assertThat(book.getPublisherName()).isEqualTo("◯◯書房");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getIsbn()).isEqualTo("0000000000001");
        assertThat(book.getSalesUnitPrice()).isEqualTo(1200);
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
    void searchIgnoresCase() {
        final var books = booksOperationService.search("spring", null, null, 0, 10);

        assertThat(books.getContent()).extracting("title").contains("Spring入門");
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchMatchesAuthorStartingWithIgnoreCase() {
        final var books = booksOperationService.search("taro", null, null, 0, 10);

        assertThat(books.getContent()).extracting("id").containsExactly(1L);
        assertThat(books.getTotalElements()).isEqualTo(1);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchDoesNotMatchTitleContainingKeyword() {
        final var books = booksOperationService.search("入門", null, null, 0, 10);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getTotalElements()).isZero();
        assertThat(books.getTotalPages()).isZero();
    }

    @Test
    void searchDoesNotMatchAuthorContainingKeyword() {
        final var books = booksOperationService.search("aro", null, null, 0, 10);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getTotalElements()).isZero();
        assertThat(books.getTotalPages()).isZero();
    }

    @Test
    void searchFiltersByReleaseDateRange() {
        final var books = booksOperationService.search("spring", LocalDate.of(2020, 1, 1), LocalDate.of(2020, 1, 1), 0, 10);

        assertThat(books.getContent()).extracting("title").contains("Spring入門");
    }

    @Test
    void searchDoesNotFilterByKeywordWhenKeywordIsNull() {
        final var books = booksOperationService.search(null, null, null, 0, 2);

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
        final var books = booksOperationService.search("   ", null, null, 0, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(1L, 2L);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(21);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void searchReturnsFirstPageAndMetadata() {
        final var books = booksOperationService.search("はじめて", null, null, 0, 2);

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
        final var books = booksOperationService.search("はじめて", null, null, 1, 2);

        assertThat(books.getContent()).extracting("id").containsExactly(4L, 5L);
    }

    @Test
    void findByIdReturnsEmptyBookStockListWhenBookHasNoStock() {
        final var book = booksOperationService.findById(6L);

        assertThat(book.getBookStockList()).isEmpty();
    }

    @Test
    void searchReturnsEmptyContentWhenPageOutOfRange() {
        final var firstPage = booksOperationService.search("はじめて", null, null, 0, 2);
        final var books = booksOperationService.search("はじめて", null, null, firstPage.getTotalPages(), 2);

        assertThat(books.getContent()).isEmpty();
        assertThat(books.getPage()).isEqualTo(firstPage.getTotalPages());
        assertThat(books.getSize()).isEqualTo(2);
        assertThat(books.getTotalElements()).isEqualTo(firstPage.getTotalElements());
        assertThat(books.getTotalPages()).isEqualTo(firstPage.getTotalPages());
    }

    @Test
    void searchAppliesReleaseDateRangeWithPaging() {
        final var books = booksOperationService.search("はじめて", LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1), 0, 3);

        assertThat(books.getContent()).extracting("id").containsExactly(2L, 3L, 4L);
        assertThat(books.getTotalElements()).isGreaterThanOrEqualTo(20);
        assertThat(books.getTotalPages()).isEqualTo(calculateTotalPages(books.getTotalElements(), books.getSize()));
    }

    @Test
    void createReturnsGeneratedIdAndResponse() {
        final var releaseDate = LocalDate.of(2021, 1, 1);
        final var book = booksOperationService.create(new BookCreateRequest("Doma入門", "Jiro", releaseDate, 2L, 5L, "9784000000301", 1400));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("Doma入門");
        assertThat(book.getAuthor()).isEqualTo("Jiro");
        assertThat(book.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(book.getPublisherId()).isEqualTo(2L);
        assertThat(book.getPublisherName()).isEqualTo("△△出版");
        assertThat(book.getGenreId()).isEqualTo(5L);
        assertThat(book.getGenreName()).isEqualTo("工学");
        assertThat(book.getIsbn()).isEqualTo("9784000000301");
        assertThat(book.getVersion()).isEqualTo(1L);
        assertThat(book.getSalesUnitPrice()).isEqualTo(1400);
    }

    @Test
    void createThrowsWhenPublisherDoesNotExist() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("Doma入門", "Jiro", LocalDate.of(2021, 1, 1), 999L, 5L, "9784000000302", 1400)))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void createThrowsWhenBookGenreDoesNotExist() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("Doma入門", "Jiro", LocalDate.of(2021, 1, 1), 1L, 999L, "9784000000303", 1400)))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void createThrowsWhenIsbnAlreadyExists() {
        assertThatThrownBy(() -> booksOperationService.create(new BookCreateRequest("Doma入門", "Jiro", LocalDate.of(2021, 1, 1), 1L, 5L, "0000000000001", 1400)))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000001)");
    }

    @Test
    void createSalesUnitPriceSchedulesFuturePrice() {
        final var effectiveFrom = LocalDate.now().plusDays(10);

        booksOperationService.createSalesUnitPrice(1L, new BookSalesUnitPriceCreateRequest(1500, effectiveFrom));

        assertThat(booksOperationService.findById(1L).getSalesUnitPrice()).isEqualTo(1200);
        assertThatThrownBy(() -> booksOperationService.createSalesUnitPrice(1L, new BookSalesUnitPriceCreateRequest(1600, effectiveFrom)))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book_sales_unit_price_history(book_id,effective_from=1," + effectiveFrom + ")");
    }

    @Test
    void updateChangesBookAndUpdateAt() {
        var before = booksOperationService.findById(1L);
        final var releaseDate = LocalDate.of(2021, 2, 1);

        final var updated = booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", releaseDate, 2L, 5L, "9784000000311", before.getVersion()));

        assertThat(updated.getTitle()).isEqualTo("Doma更新");
        assertThat(updated.getAuthor()).isEqualTo("Saburo");
        assertThat(updated.getReleaseDate()).isEqualTo(releaseDate);
        assertThat(updated.getPublisherId()).isEqualTo(2L);
        assertThat(updated.getPublisherName()).isEqualTo("△△出版");
        assertThat(updated.getGenreId()).isEqualTo(5L);
        assertThat(updated.getGenreName()).isEqualTo("工学");
        assertThat(updated.getIsbn()).isEqualTo("9784000000311");
        assertThat(updated.getUpdateAt()).isAfter(before.getUpdateAt());
        assertThat(updated.getVersion()).isEqualTo(before.getVersion() + 1);
    }

    @Test
    void updateThrowsWhenVersionIsStale() {
        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "9784000000312", -1L)))
            .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void updateThrowsWhenPublisherDoesNotExist() {
        final var before = booksOperationService.findById(1L);

        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 999L, 5L, "9784000000313", before.getVersion())))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: publisher(id=999)");
    }

    @Test
    void updateThrowsWhenBookGenreDoesNotExist() {
        final var before = booksOperationService.findById(1L);

        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 999L, "9784000000314", before.getVersion())))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book_genre(id=999)");
    }

    @Test
    void updateThrowsWhenIsbnAlreadyExistsInAnotherBook() {
        final var before = booksOperationService.findById(1L);

        assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "0000000000002", before.getVersion())))
            .isInstanceOf(UniqueConstraintValidationException.class)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000002)");
    }

    @Test
    void updateAllowsKeepingCurrentIsbn() {
        final var before = booksOperationService.findById(1L);

        final var updated = booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, before.getIsbn(), before.getVersion()));

        assertThat(updated.getIsbn()).isEqualTo(before.getIsbn());
    }

    @Test
    void updateThrowsWhenWriteLockCannotBeAcquired() throws Exception {
        final var before = booksOperationService.findById(1L);

        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "Doma更新", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "9784000000315", before.getVersion())))
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
