package com.example.demo.jooq.service

import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookPageResponse
import com.example.demo.api.response.BookResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.exception.RepositoryDataNotfoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.jooq.converter.BookOperationConverterJooq
import com.example.demo.jooq.dsl.BookOperationDsl
import com.example.demo.jooq.generated.Tables
import com.example.demo.jooq.validator.BookDataValidatorJooq
import com.example.demo.service.BooksOperationService
import com.example.demo.util.PageCalculator.calculateOffset
import com.example.demo.util.PageCalculator.calculateTotalPages
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("jooq")
class BooksOperationServiceJooq(
    private val bookOperationDsl: BookOperationDsl,
    private val converter: BookOperationConverterJooq,
    private val dataValidator: BookDataValidatorJooq
) : BooksOperationService {
    @Transactional(readOnly = true)
    override fun findById(id: Long): BookResponse? {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        val rows = bookOperationDsl.selectByIdWithPublisherName(id)
        if (rows.isEmpty()) {
            throw RepositoryDataNotfoundException()
        }
        return converter.toResponse(rows)
    }

    @Transactional(readOnly = true)
    override fun search(
        keyword: String?,
        releaseDateFrom: LocalDate?,
        releaseDateTo: LocalDate?,
        page: Int,
        size: Int
    ): BookPageResponse {
        val offset = calculateOffset(page, size)
        val condition = bookOperationDsl.searchCondition(keyword, releaseDateFrom, releaseDateTo)
        val rows = bookOperationDsl.selectByTitleOrAuthorStartingWithIgnoreCase(condition, size, offset)
        val totalElements = bookOperationDsl.totalElements(condition).toLong()
        val response = BookPageResponse()
        response.content = converter.toResponseList(rows)
        response.page = page
        response.size = size
        response.totalElements = totalElements
        response.totalPages = calculateTotalPages(totalElements, size)
        return response
    }

    @Transactional
    override fun create(request: BookCreateRequest): BookResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        dataValidator.foreignKeyValidate(request.publisherId, request.genreId)
        dataValidator.uniqueIsbnValidate(request.isbn, null)
        val id = bookOperationDsl.insert(request)
        bookOperationDsl.insertSalesUnitPriceHistory(
            id!!,
            request.salesUnitPrice,
            request.releaseDate,
            null,
            LocalDateTime.now()
        )
        return findById(id!!)
    }

    @RetryableOnLockFailure
    @Transactional
    override fun update(request: BookUpdateRequest): BookResponse? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        dataValidator.foreignKeyValidate(request.publisherId, request.genreId)
        val book = bookOperationDsl.selectBookForUpdate(request.id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        val currentVersion = book!!.get<Long>(Tables.BOOK.VERSION)
        dataValidator.versionValidate(request.id!!, currentVersion, request.version)
        dataValidator.uniqueIsbnValidate(request.isbn, request.id)
        bookOperationDsl.update(request, currentVersion)
        return findById(request.id!!)
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
        val book = bookOperationDsl.selectBookForUpdate(bookId)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        val followingHistories = bookOperationDsl.selectFollowingSalesUnitPriceHistories(bookId, request.effectiveFrom)
        if (!followingHistories.isEmpty() && followingHistories.first()!!
                .effectiveFrom == request.effectiveFrom
        ) {
            throw UniqueConstraintValidationException(
                "book_sales_unit_price_history",
                "book_id,effective_from",
                bookId.toString() + "," + request.effectiveFrom
            )
        }
        val previousHistory = bookOperationDsl.selectPreviousSalesUnitPriceHistory(bookId, request.effectiveFrom)
        if (Objects.isNull(previousHistory)) {
            throw RepositoryDataNotfoundException()
        }
        bookOperationDsl.updateSalesUnitPriceHistoryEffectiveTo(previousHistory!!, request.effectiveFrom!!.minusDays(1))
        val effectiveTo: LocalDate? =
            if (followingHistories.isEmpty()) null else followingHistories.first()!!.effectiveFrom!!.minusDays(1)
        bookOperationDsl.insertSalesUnitPriceHistory(
            bookId,
            request.salesUnitPrice,
            request.effectiveFrom,
            effectiveTo,
            LocalDateTime.now()
        )
    }

    @RetryableOnLockFailure
    @Transactional
    override fun delete(id: Long) {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        val book = bookOperationDsl.selectBookForUpdate(id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        bookOperationDsl.delete(id)
    }
}
