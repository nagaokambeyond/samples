package com.example.demo.jooq.validator

import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.jooq.dsl.BookDsl
import com.example.demo.jooq.dsl.BookGenreDsl
import com.example.demo.jooq.dsl.PublisherDsl
import com.example.demo.jooq.entity.BookWithStockRow
import org.springframework.context.annotation.Profile
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("jooq")
class BookDataValidatorJooq(
    private val bookDsl: BookDsl,
    private val publisherDsl: PublisherDsl,
    private val bookGenreDsl: BookGenreDsl
) {
    fun foreignKeyValidate(publisherId: Long?, genreId: Long?) {
        if (!publisherDsl.existsPublisher(publisherId)) {
            throw ForeignKeyReferenceNotFoundException("publisher", publisherId)
        }
        if (!bookGenreDsl.exists(genreId)) {
            throw ForeignKeyReferenceNotFoundException("book_genre", genreId)
        }
    }

    fun versionValidate(bookId: Long, currentVersion: Long?, requestVersion: Long?) {
        if (currentVersion != requestVersion) {
            throw ObjectOptimisticLockingFailureException(BookWithStockRow::class.java, bookId)
        }
    }

    fun uniqueIsbnValidate(isbn: String?, bookId: Long?) {
        val existingBookId = bookDsl.selectIdByIsbn(isbn)
        if (Objects.nonNull(existingBookId) && existingBookId != bookId) {
            throw UniqueConstraintValidationException("book", "isbn", isbn)
        }
    }
}
