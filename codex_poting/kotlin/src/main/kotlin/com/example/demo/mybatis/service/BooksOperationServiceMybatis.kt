package com.example.demo.mybatis.service

import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookSalesUnitPriceCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.api.response.BookPageResponse
import com.example.demo.api.response.BookResponse
import com.example.demo.config.RetryableOnLockFailure
import com.example.demo.exception.RepositoryDataNotfoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.mybatis.converter.BookOperationConverterMybatis
import com.example.demo.mybatis.entity.BookWithPublisherName
import com.example.demo.mybatis.generator.entity.BookEntity
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntity
import com.example.demo.mybatis.generator.entity.BookSalesUnitPriceHistoryEntityExample
import com.example.demo.mybatis.generator.mapper.BookMapper
import com.example.demo.mybatis.generator.mapper.BookSalesUnitPriceHistoryMapper
import com.example.demo.mybatis.mapper.BookCustomMapper
import com.example.demo.mybatis.validator.BookDataValidatorMybatis
import com.example.demo.service.BooksOperationService
import com.example.demo.util.PageCalculator
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
@Profile("mybatis")
class BooksOperationServiceMybatis(
    private val bookMapper: BookMapper,
    private val bookSalesUnitPriceHistoryMapper: BookSalesUnitPriceHistoryMapper,
    private val bookCustomMapper: BookCustomMapper,
    private val converter: BookOperationConverterMybatis,
    private val dataValidator: BookDataValidatorMybatis
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
        val books = bookCustomMapper.selectByTitleOrAuthorStartingWithIgnoreCase(
            keyword,
            releaseDateFrom,
            releaseDateTo,
            size,
            offset
        )
        val totalElements =
            bookCustomMapper.countByTitleOrAuthorStartingWithIgnoreCase(keyword, releaseDateFrom, releaseDateTo)
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
        val book = BookEntity()
        book.setTitle(request.title)
        book.setAuthor(request.author)
        book.setReleaseDate(request.releaseDate)
        book.setPublisherId(request.publisherId)
        book.setGenreId(request.genreId)
        book.setIsbn(request.isbn)
        book.setCreateAt(now)
        book.setUpdateAt(now)
        book.setVersion(1L)
        bookCustomMapper.insertWithGeneratedKey(book)
        bookCustomMapper.insertSalesUnitPriceHistoryWithId(
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
        val book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(request.id)
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
        book.setVersion(book.getVersion() + 1)
        bookMapper.updateByPrimaryKey(book)
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
        val book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(bookId)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        val followingHistories = selectFollowingHistories(book!!.getId(), request.effectiveFrom)
        if (!followingHistories.isEmpty() && followingHistories.first()!!
                .getEffectiveFrom() == request.effectiveFrom
        ) {
            throw UniqueConstraintValidationException(
                "book_sales_unit_price_history",
                "book_id,effective_from",
                book.getId().toString() + "," + request.effectiveFrom
            )
        }
        val previousHistory = selectPreviousHistory(book.getId(), request.effectiveFrom)
        if (Objects.isNull(previousHistory)) {
            throw RepositoryDataNotfoundException()
        }
        val now = LocalDateTime.now()
        previousHistory!!.setEffectiveTo(request.effectiveFrom!!.minusDays(1))
        previousHistory.setUpdateAt(now)
        previousHistory.setVersion(previousHistory.getVersion() + 1)
        bookSalesUnitPriceHistoryMapper.updateByPrimaryKey(previousHistory)
        val effectiveTo: LocalDate? =
            if (followingHistories.isEmpty()) null else followingHistories.first()!!.getEffectiveFrom().minusDays(1)
        bookCustomMapper.insertSalesUnitPriceHistoryWithId(
            toSalesUnitPriceHistory(
                book.getId(),
                request.salesUnitPrice,
                request.effectiveFrom,
                effectiveTo,
                now
            )
        )
    }

    @RetryableOnLockFailure
    @Transactional
    override fun delete(id: Long) {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        val book = bookCustomMapper.selectByPrimaryKeyWithWriteLock(id)
        if (Objects.isNull(book)) {
            throw RepositoryDataNotfoundException()
        }
        bookMapper.deleteByPrimaryKey(book!!.getId())
    }

    private fun findByIdWithPublisherName(id: Long?): BookWithPublisherName {
        val book = bookCustomMapper.selectByPrimaryKeyWithPublisherName(id)
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
    ): BookSalesUnitPriceHistoryEntity {
        val history = BookSalesUnitPriceHistoryEntity()
        history.setId(bookCustomMapper.selectNextSalesUnitPriceHistoryId())
        history.setBookId(bookId)
        history.setSalesUnitPrice(salesUnitPrice)
        history.setEffectiveFrom(effectiveFrom)
        history.setEffectiveTo(effectiveTo)
        history.setCreateAt(now)
        history.setUpdateAt(now)
        history.setVersion(1L)
        return history
    }

    private fun selectFollowingHistories(
        bookId: Long?,
        effectiveFrom: LocalDate?
    ): MutableList<BookSalesUnitPriceHistoryEntity?> {
        val example = BookSalesUnitPriceHistoryEntityExample()
        example.createCriteria().andBookIdEqualTo(bookId).andEffectiveFromGreaterThanOrEqualTo(effectiveFrom)
        example.setOrderByClause("effective_from")
        return bookSalesUnitPriceHistoryMapper.selectByExample(example)
    }

    private fun selectPreviousHistory(bookId: Long?, effectiveFrom: LocalDate?): BookSalesUnitPriceHistoryEntity? {
        val example = BookSalesUnitPriceHistoryEntityExample()
        example.createCriteria().andBookIdEqualTo(bookId).andEffectiveFromLessThan(effectiveFrom)
        example.setOrderByClause("effective_from desc")
        val histories = bookSalesUnitPriceHistoryMapper.selectByExample(example)
        return if (histories.isEmpty()) null else histories.first()
    }
}
