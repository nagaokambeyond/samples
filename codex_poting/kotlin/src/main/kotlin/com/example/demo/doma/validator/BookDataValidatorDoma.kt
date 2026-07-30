package com.example.demo.doma.validator

import com.example.demo.doma.dao.BookCustomDao
import com.example.demo.doma.generator.dao.BookGenreDao
import com.example.demo.doma.generator.dao.PublisherDao
import com.example.demo.doma.generator.entity.Book
import com.example.demo.doma.generator.entity.BookGenre
import com.example.demo.doma.generator.entity.Publisher
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.UniqueConstraintValidationException
import org.springframework.context.annotation.Profile
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("doma")
class BookDataValidatorDoma(
    private val publisherDao: PublisherDao,
    private val bookGenreDao: BookGenreDao,
    private val bookCustomDao: BookCustomDao
) {
    fun foreignKeyValidate(publisherId: Long?, genreId: Long?) {
        val publisher = publisherDao.selectById(publisherId)
        if (Objects.isNull(publisher)) {
            throw ForeignKeyReferenceNotFoundException(Publisher::class.java, publisherId)
        }
        val bookGenre = bookGenreDao.selectById(genreId)
        if (Objects.isNull(bookGenre)) {
            throw ForeignKeyReferenceNotFoundException(BookGenre::class.java, genreId)
        }
    }

    fun versionValidate(book: Book, requestVersion: Long?) {
        if (book.getVersion() != requestVersion) {
            throw ObjectOptimisticLockingFailureException(Book::class.java, book.getId())
        }
    }

    fun uniqueIsbnValidate(isbn: String?, bookId: Long?) {
        val book = bookCustomDao.selectByIsbn(isbn)
        if (Objects.nonNull(book) && book!!.getId() != bookId) {
            throw UniqueConstraintValidationException("book", "isbn", isbn)
        }
    }
}
