package com.example.demo.doma.service

import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookPageResponse
import com.example.demo.api.response.BookResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.doma.converter.BookOperationConverterDoma
import com.example.demo.doma.dao.BookCustomDao
import com.example.demo.doma.dao.BookSalesUnitPriceHistoryCustomDao
import com.example.demo.doma.entity.BookWithPublisherName
import com.example.demo.doma.generator.dao.BookDao
import com.example.demo.doma.generator.dao.BookSalesUnitPriceHistoryDao
import com.example.demo.doma.generator.entity.Book
import com.example.demo.doma.generator.entity.BookSalesUnitPriceHistory
import com.example.demo.doma.validator.BookDataValidatorDoma
import com.example.demo.exception.RepositoryDataNotfoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.service.BooksOperationService
import com.example.demo.util.PageCalculator
import org.seasar.doma.jdbc.OptimisticLockException
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("doma")
@Primary
class BooksOperationServiceDoma(
    private val bookDao: BookDao,
    private val bookSalesUnitPriceHistoryDao: BookSalesUnitPriceHistoryDao,
    private val bookSalesUnitPriceHistoryCustomDao: BookSalesUnitPriceHistoryCustomDao,
    private val bookCustomDao: BookCustomDao,
    private val converter: BookOperationConverterDoma,
    private val dataValidator: BookDataValidatorDoma
) : BooksOperationService {
    @Transactional(readOnly = true)
    override fun findById(id: Long): BookResponse? {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        return converter.toResponse(findByIdWithPublisherName(id))
    }

    @Transactional(readOnly = true)
    override fun search(
        keyword: String?,
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?,
        page: Int,
        size: Int
    ): BookPageResponse {
        val offset = PageCalculator.calculateOffset(page, size)
        val books = bookCustomDao.selectByTitleOrAuthorStartingWithIgnoreCase(
            keyword,
            releaseDateFrom,
            releaseDateTo,
            size,
            offset
        )
        val totalElements =
            bookCustomDao.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo)
        val response = BookPageResponse()
        response.content = converter.toResponse(books.orEmpty().toMutableList())
        response.page = page
        response.size = size
        response.totalElements = totalElements
        response.totalPages = PageCalculator.calculateTotalPages(totalElements, size)
        return response
    }

    @Transactional
    override fun create(request: BookCreateRequest): BookResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        dataValidator.foreignKeyValidate(request.publisherId, request.genreId)
        dataValidator.uniqueIsbnValidate(request.isbn, null)
        val now = LocalDateTime.now()
        val book = Book()
        book.setTitle(request.title)
        book.setAuthor(request.author)
        book.setReleaseDate(request.releaseDate)
        book.setPublisherId(request.publisherId)
        book.setGenreId(request.genreId)
        book.setIsbn(request.isbn)
        book.setCreateAt(now)
        book.setUpdateAt(now)
        book.setVersion(1L)
        bookDao.insert(book)
        bookSalesUnitPriceHistoryCustomDao.insertWithId(
            toSalesUnitPriceHistory(
                book.getId(),
                request.salesUnitPrice,
                request.releaseDate,
                null,
                now
            )
        )
        return findById(book.getId())
    }

    @RetryableOnLockFailure
    @Transactional
    override fun update(request: BookUpdateRequest): BookResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        dataValidator.foreignKeyValidate(request.publisherId, request.genreId)
        val book = bookCustomDao.selectByIdWithWriteLock(request.id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        dataValidator.versionValidate(book!!, request.version)
        dataValidator.uniqueIsbnValidate(request.isbn, book.getId())
        book.setTitle(request.title)
        book.setAuthor(request.author)
        book.setReleaseDate(request.releaseDate)
        book.setPublisherId(request.publisherId)
        book.setGenreId(request.genreId)
        book.setIsbn(request.isbn)
        book.setUpdateAt(LocalDateTime.now())
        try {
            bookDao.update(book)
        } catch (ex: OptimisticLockException) {
            throw ObjectOptimisticLockingFailureException(Book::class.java, book.getId(), ex)
        }
        return findById(book.getId())
    }

    @RetryableOnLockFailure
    @Transactional
    override fun createSalesUnitPrice(bookId: Long, request: BookSalesUnitPriceCreateRequest) {
        if (bookId == null) {
            throw NullPointerException("bookId is marked non-null but is null")
        }
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        val book = bookCustomDao.selectByIdWithWriteLock(bookId)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        val followingHistories =
            bookSalesUnitPriceHistoryCustomDao.selectFollowingHistories(book!!.getId(), request.effectiveFrom)
        if (!followingHistories.isNullOrEmpty() && followingHistories.first()!!
                .getEffectiveFrom() == request.effectiveFrom
        ) {
            throw UniqueConstraintValidationException(
                "book_sales_unit_price_history",
                "book_id,effective_from",
                book.getId().toString() + "," + request.effectiveFrom
            )
        }
        val previousHistory =
            bookSalesUnitPriceHistoryCustomDao.selectPreviousHistory(book.getId(), request.effectiveFrom)
        if (Objects.isNull(previousHistory)) {
            throw RepositoryDataNotfoundException()
        }
        previousHistory!!.setEffectiveTo(request.effectiveFrom!!.minusDays(1))
        previousHistory.setUpdateAt(LocalDateTime.now())
        try {
            bookSalesUnitPriceHistoryDao.update(previousHistory)
        } catch (ex: OptimisticLockException) {
            throw ObjectOptimisticLockingFailureException(
                BookSalesUnitPriceHistory::class.java,
                previousHistory.getId(),
                ex
            )
        }
        val effectiveTo: LocalDate? =
            if (followingHistories.isNullOrEmpty()) null else followingHistories.first()!!.getEffectiveFrom().minusDays(1)
        bookSalesUnitPriceHistoryCustomDao.insertWithId(
            toSalesUnitPriceHistory(
                book.getId(),
                request.salesUnitPrice,
                request.effectiveFrom,
                effectiveTo,
                LocalDateTime.now()
            )
        )
    }

    @RetryableOnLockFailure
    @Transactional
    override fun delete(id: Long) {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        val book = bookCustomDao.selectByIdWithWriteLock(id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        try {
            bookDao.delete(book)
        } catch (ex: OptimisticLockException) {
            throw ObjectOptimisticLockingFailureException(Book::class.java, book!!.getId(), ex)
        }
    }

    private fun findByIdWithPublisherName(id: Long?): BookWithPublisherName {
        val book = bookCustomDao.selectByIdWithPublisherName(id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        return book!!
    }

    private fun toSalesUnitPriceHistory(
        bookId: Long?,
        salesUnitPrice: Int?,
        effectiveFrom: LocalDate?,
        effectiveTo: LocalDate?,
        now: LocalDateTime?
    ): BookSalesUnitPriceHistory {
        val history = BookSalesUnitPriceHistory()
        history.setId(bookSalesUnitPriceHistoryCustomDao.selectNextId())
        history.setBookId(bookId)
        history.setSalesUnitPrice(salesUnitPrice)
        history.setEffectiveFrom(effectiveFrom)
        history.setEffectiveTo(effectiveTo)
        history.setCreateAt(now)
        history.setUpdateAt(now)
        history.setVersion(1L)
        return history
    }
}
