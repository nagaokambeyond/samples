package com.example.demo.mybatis.validator

import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.mybatis.generator.entity.BookEntity
import com.example.demo.mybatis.generator.entity.BookEntityExample
import com.example.demo.mybatis.generator.entity.BookGenreEntity
import com.example.demo.mybatis.generator.entity.PublisherEntity
import com.example.demo.mybatis.generator.mapper.BookGenreMapper
import com.example.demo.mybatis.generator.mapper.BookMapper
import com.example.demo.mybatis.generator.mapper.PublisherMapper
import org.springframework.context.annotation.Profile
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer

@Component
@Profile("mybatis")
class BookDataValidatorMybatis(
    private val bookMapper: BookMapper,
    private val publisherMapper: PublisherMapper,
    private val bookGenreMapper: BookGenreMapper
) {
    fun foreignKeyValidate(publisherId: Long?, genreId: Long?) {
        val publisher = publisherMapper.selectByPrimaryKey(publisherId)
        if (Objects.isNull(publisher)) {
            throw ForeignKeyReferenceNotFoundException(PublisherEntity::class.java, publisherId)
        }
        val bookGenre = bookGenreMapper.selectByPrimaryKey(genreId)
        if (Objects.isNull(bookGenre)) {
            throw ForeignKeyReferenceNotFoundException(BookGenreEntity::class.java, genreId)
        }
    }

    fun versionValidate(book: BookEntity, requestVersion: Long?) {
        if (book.getVersion() != requestVersion) {
            throw ObjectOptimisticLockingFailureException(BookEntity::class.java, book.getId())
        }
    }

    fun uniqueIsbnValidate(isbn: String?, bookId: Long?) {
        val example = BookEntityExample()
        example.createCriteria().andIsbnEqualTo(isbn)
        bookMapper.selectByExample(example).stream().filter { book: BookEntity? -> book!!.getId() != bookId }
            .findFirst().ifPresent(
                Consumer { book: BookEntity? ->
                    throw UniqueConstraintValidationException("book", "isbn", isbn)
                })
    }
}
