package com.example.demo.jooq.service

import com.example.demo.BookRowLock
import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookResponse
import com.example.demo.api.response.BookStockResponse
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.RepositoryDataNotfoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.service.BooksOperationService
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("jooq")
@Transactional
internal class BooksOperationServiceJooqTest {
    @Autowired
    private val primaryBooksOperationService: BooksOperationService? = null

    @Autowired
    private val booksOperationService: BooksOperationServiceJooq? = null

    @Autowired
    private val dataSource: DataSource? = null

    @Test
    fun usesJooqAsPrimaryBookService() {
        Assertions.assertThat(primaryBooksOperationService)
            .isInstanceOf(BooksOperationServiceJooq::class.java)
    }

    @Test
    fun findByIdReturnsBook() {
        val book = booksOperationService!!.findById(1L)

        Assertions.assertThat(book!!.id).isEqualTo(1L)
        Assertions.assertThat(book.title).isEqualTo("Spring入門")
        Assertions.assertThat(book.publisherId).isEqualTo(1L)
        Assertions.assertThat(book.publisherName).isEqualTo("◯◯書房")
        Assertions.assertThat(book.genreId).isEqualTo(5L)
        Assertions.assertThat(book.genreName).isEqualTo("工学")
        Assertions.assertThat(book.isbn).isEqualTo("0000000000001")
        Assertions.assertThat(book.salesUnitPrice).isEqualTo(1200)
        Assertions.assertThat(book.bookStockList)
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                Assertions.tuple(1L, 1L, "あ駅前店", 10),
                Assertions.tuple(2L, 2L, "い駅前店", 20),
                Assertions.tuple(3L, 3L, "う駅前店", 30)
            )
    }

    @Test
    fun findByIdThrowsWhenBookDoesNotExist() {
        Assertions.assertThatThrownBy(ThrowingCallable { booksOperationService!!.findById(999L) })
            .isInstanceOf(RepositoryDataNotfoundException::class.java)
    }

    @Test
    fun searchIgnoresCaseAndMatchesStartingKeyword() {
        val books = booksOperationService!!.search("spring", null, null, 0, 10)

        Assertions.assertThat(books.content).extracting("title").contains("Spring入門")
        Assertions.assertThat(books.totalElements).isGreaterThanOrEqualTo(1)
        Assertions.assertThat(books.totalPages).isEqualTo(calculateTotalPages(books.totalElements, books.size))
    }

    @Test
    fun searchDoesNotMatchContainingKeyword() {
        val books = booksOperationService!!.search("入門", null, null, 0, 10)

        Assertions.assertThat(books.content).isEmpty()
        Assertions.assertThat(books.totalElements).isZero()
        Assertions.assertThat(books.totalPages).isZero()
    }

    @Test
    fun searchDoesNotFilterByBlankKeywordAndAggregatesStock() {
        val books = booksOperationService!!.search("   ", null, null, 0, 2)

        Assertions.assertThat(books.content).extracting("id").containsExactly(1L, 2L)
        Assertions.assertThat(books.content!!.first().bookStockList)
            .extracting("id", "bookStockStoreId", "storeName", "bookStockQuantity")
            .containsExactly(
                Assertions.tuple(1L, 1L, "あ駅前店", 10),
                Assertions.tuple(2L, 2L, "い駅前店", 20),
                Assertions.tuple(3L, 3L, "う駅前店", 30)
            )
        Assertions.assertThat(books.totalElements).isGreaterThanOrEqualTo(21)
        Assertions.assertThat(books.totalPages).isEqualTo(calculateTotalPages(books.totalElements, books.size))
    }

    @Test
    fun searchAppliesReleaseDateRangeWithPaging() {
        val books = booksOperationService!!.search("はじめて", LocalDate.of(2020, 2, 1), LocalDate.of(2020, 2, 1), 1, 2)

        Assertions.assertThat(books.content).extracting("id").containsExactly(4L, 5L)
        Assertions.assertThat(books.page).isEqualTo(1)
        Assertions.assertThat(books.size).isEqualTo(2)
        Assertions.assertThat(books.totalElements).isGreaterThanOrEqualTo(20)
    }

    @Test
    fun createReturnsGeneratedIdAndResponse() {
        val releaseDate = LocalDate.of(2021, 1, 1)
        val book = booksOperationService!!.create(
            BookCreateRequest(
                "jOOQ入門",
                "Jiro",
                releaseDate,
                2L,
                5L,
                "9784000000401",
                1400
            )
        )

        Assertions.assertThat(book!!.id).isNotNull()
        Assertions.assertThat(book.title).isEqualTo("jOOQ入門")
        Assertions.assertThat(book.author).isEqualTo("Jiro")
        Assertions.assertThat(book.releaseDate).isEqualTo(releaseDate)
        Assertions.assertThat(book.publisherId).isEqualTo(2L)
        Assertions.assertThat(book.publisherName).isEqualTo("△△出版")
        Assertions.assertThat(book.genreId).isEqualTo(5L)
        Assertions.assertThat(book.genreName).isEqualTo("工学")
        Assertions.assertThat(book.isbn).isEqualTo("9784000000401")
        Assertions.assertThat(book.version).isEqualTo(1L)
        Assertions.assertThat(book.salesUnitPrice).isEqualTo(1400)
    }

    @Test
    fun createThrowsWhenPublisherDoesNotExist() {
        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService!!.create(
                BookCreateRequest(
                    "jOOQ入門",
                    "Jiro",
                    LocalDate.of(2021, 1, 1),
                    999L,
                    5L,
                    "9784000000402",
                    1400
                )
            )
        })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: publisher(id=999)")
    }

    @Test
    fun createThrowsWhenBookGenreDoesNotExist() {
        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService!!.create(
                BookCreateRequest(
                    "jOOQ入門",
                    "Jiro",
                    LocalDate.of(2021, 1, 1),
                    1L,
                    999L,
                    "9784000000403",
                    1400
                )
            )
        })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book_genre(id=999)")
    }

    @Test
    fun createThrowsWhenIsbnAlreadyExists() {
        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService!!.create(
                BookCreateRequest(
                    "jOOQ入門",
                    "Jiro",
                    LocalDate.of(2021, 1, 1),
                    1L,
                    5L,
                    "0000000000001",
                    1400
                )
            )
        })
            .isInstanceOf(UniqueConstraintValidationException::class.java)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000001)")
    }

    @Test
    fun createSalesUnitPriceSchedulesFuturePrice() {
        val effectiveFrom = LocalDate.now().plusDays(10)

        booksOperationService!!.createSalesUnitPrice(1L, BookSalesUnitPriceCreateRequest(1500, effectiveFrom))

        Assertions.assertThat(booksOperationService.findById(1L)!!.salesUnitPrice).isEqualTo(1200)
        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService.createSalesUnitPrice(
                1L,
                BookSalesUnitPriceCreateRequest(1600, effectiveFrom)
            )
        })
            .isInstanceOf(UniqueConstraintValidationException::class.java)
            .hasMessage("一意制約に違反しています: book_sales_unit_price_history(book_id,effective_from=1," + effectiveFrom + ")")
    }

    @Test
    fun updateChangesBookAndUpdateAt() {
        val before = booksOperationService!!.findById(1L)
        val releaseDate = LocalDate.of(2021, 2, 1)

        val updated = booksOperationService.update(
            BookUpdateRequest(
                1L,
                "jOOQ更新",
                "Saburo",
                releaseDate,
                2L,
                5L,
                "9784000000411",
                before!!.version
            )
        )

        Assertions.assertThat(updated!!.title).isEqualTo("jOOQ更新")
        Assertions.assertThat(updated.author).isEqualTo("Saburo")
        Assertions.assertThat(updated.releaseDate).isEqualTo(releaseDate)
        Assertions.assertThat(updated.isbn).isEqualTo("9784000000411")
        Assertions.assertThat(updated.publisherId).isEqualTo(2L)
        Assertions.assertThat(updated.publisherName).isEqualTo("△△出版")
        Assertions.assertThat(updated.updateAt).isAfter(before.updateAt)
        Assertions.assertThat(updated.version).isEqualTo(before.version!! + 1)
    }

    @Test
    fun updateThrowsWhenPublisherDoesNotExist() {
        val before = booksOperationService!!.findById(1L)

        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService.update(
                BookUpdateRequest(
                    1L,
                    "jOOQ更新",
                    "Saburo",
                    LocalDate.of(2021, 2, 1),
                    999L,
                    5L,
                    "9784000000412",
                    before!!.version
                )
            )
        })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: publisher(id=999)")
    }

    @Test
    fun updateThrowsWhenBookGenreDoesNotExist() {
        val before = booksOperationService!!.findById(1L)

        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService.update(
                BookUpdateRequest(
                    1L,
                    "jOOQ更新",
                    "Saburo",
                    LocalDate.of(2021, 2, 1),
                    1L,
                    999L,
                    "9784000000412",
                    before!!.version
                )
            )
        })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book_genre(id=999)")
    }

    @Test
    fun updateThrowsWhenVersionIsStale() {
        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService!!.update(
                BookUpdateRequest(
                    1L,
                    "jOOQ更新",
                    "Saburo",
                    LocalDate.of(2021, 2, 1),
                    1L,
                    5L,
                    "9784000000412",
                    -1L
                )
            )
        })
            .isInstanceOf(ObjectOptimisticLockingFailureException::class.java)
    }

    @Test
    fun updateThrowsWhenIsbnAlreadyExistsInAnotherBook() {
        val before = booksOperationService!!.findById(1L)

        Assertions.assertThatThrownBy(ThrowingCallable {
            booksOperationService.update(
                BookUpdateRequest(
                    1L,
                    "jOOQ更新",
                    "Saburo",
                    LocalDate.of(2021, 2, 1),
                    1L,
                    5L,
                    "0000000000002",
                    before!!.version
                )
            )
        })
            .isInstanceOf(UniqueConstraintValidationException::class.java)
            .hasMessage("一意制約に違反しています: book(isbn=0000000000002)")
    }

    @Test
    fun updateAllowsKeepingCurrentIsbn() {
        val before = booksOperationService!!.findById(1L)

        val updated = booksOperationService.update(
            BookUpdateRequest(
                1L,
                "jOOQ更新",
                "Saburo",
                LocalDate.of(2021, 2, 1),
                1L,
                5L,
                before!!.isbn,
                before.version
            )
        )

        Assertions.assertThat(updated!!.isbn).isEqualTo(before.isbn)
    }

    @Test
    @Throws(Exception::class)
    fun updateThrowsWhenWriteLockCannotBeAcquired() {
        val before = booksOperationService!!.findById(1L)

        BookRowLock.acquire(dataSource!!, 1L).use { ignored ->
            Assertions.assertThatThrownBy(ThrowingCallable {
                booksOperationService.update(
                    BookUpdateRequest(
                        1L,
                        "jOOQ更新",
                        "Saburo",
                        LocalDate.of(2021, 2, 1),
                        1L,
                        5L,
                        "9784000000413",
                        before!!.version
                    )
                )
            })
                .isInstanceOf(PessimisticLockingFailureException::class.java)
        }
    }

    @Test
    fun deleteRemovesBook() {
        booksOperationService!!.delete(1L)

        Assertions.assertThatThrownBy(ThrowingCallable { booksOperationService.findById(1L) })
            .isInstanceOf(RepositoryDataNotfoundException::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun deleteThrowsWhenWriteLockCannotBeAcquired() {
        BookRowLock.acquire(dataSource!!, 1L).use { ignored ->
            Assertions.assertThatThrownBy(ThrowingCallable { booksOperationService!!.delete(1L) })
                .isInstanceOf(PessimisticLockingFailureException::class.java)
        }
    }

    private fun calculateTotalPages(totalElements: Long, size: Int): Int {
        if (totalElements == 0L) {
            return 0
        }
        return ((totalElements + size - 1) / size).toInt()
    }
}
