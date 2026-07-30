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
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.springframework.orm.ObjectOptimisticLockingFailureException
import java.util.List

internal class BookDataValidatorMybatisTest {
    private var bookMapper: BookMapper? = null
    private var publisherMapper: PublisherMapper? = null
    private var bookGenreMapper: BookGenreMapper? = null
    private var validator: BookDataValidatorMybatis? = null

    @BeforeEach
    fun setUp() {
        bookMapper = Mockito.mock<BookMapper>(BookMapper::class.java)
        publisherMapper = Mockito.mock<PublisherMapper>(PublisherMapper::class.java)
        bookGenreMapper = Mockito.mock<BookGenreMapper>(BookGenreMapper::class.java)
        validator = BookDataValidatorMybatis(bookMapper!!, publisherMapper!!, bookGenreMapper!!)
    }

    @Test
    fun foreignKeyValidateAllowsExistingPublisherAndGenre() {
        Mockito.`when`<PublisherEntity?>(publisherMapper!!.selectByPrimaryKey(1L)).thenReturn(PublisherEntity())
        Mockito.`when`<BookGenreEntity?>(bookGenreMapper!!.selectByPrimaryKey(5L)).thenReturn(BookGenreEntity())

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(1L, 5L) })
    }

    @Test
    fun foreignKeyValidateThrowsWhenPublisherDoesNotExist() {
        Mockito.`when`<PublisherEntity?>(publisherMapper!!.selectByPrimaryKey(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(999L, 5L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: publisher(id=999)")
        Mockito.verifyNoInteractions(bookGenreMapper)
    }

    @Test
    fun foreignKeyValidateThrowsWhenBookGenreDoesNotExist() {
        Mockito.`when`<PublisherEntity?>(publisherMapper!!.selectByPrimaryKey(1L)).thenReturn(PublisherEntity())
        Mockito.`when`<BookGenreEntity?>(bookGenreMapper!!.selectByPrimaryKey(999L)).thenReturn(null)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(1L, 999L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book_genre(id=999)")
    }

    @Test
    fun versionValidateAllowsMatchingVersion() {
        val book = book(1L, 2L)

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { validator!!.versionValidate(book, 2L) })
    }

    @Test
    fun versionValidateThrowsWhenVersionIsStale() {
        val book = book(1L, 2L)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.versionValidate(book, 1L) })
            .isInstanceOf(ObjectOptimisticLockingFailureException::class.java)
            .satisfies({ exception: Throwable? ->
                Assertions.assertThat((exception as ObjectOptimisticLockingFailureException).getPersistentClass())
                    .isEqualTo(BookEntity::class.java)
            })
    }

    @Test
    fun uniqueIsbnValidateAllowsUnusedIsbn() {
        Mockito.`when`<MutableList<BookEntity?>?>(
            bookMapper!!.selectByExample(
                ArgumentMatchers.any<BookEntityExample?>(
                    BookEntityExample::class.java
                )
            )
        ).thenReturn(mutableListOf<BookEntity?>())

        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
    }

    @Test
    fun uniqueIsbnValidateAllowsKeepingCurrentBookIsbn() {
        Mockito.`when`<MutableList<BookEntity?>?>(
            bookMapper!!.selectByExample(
                ArgumentMatchers.any<BookEntityExample?>(
                    BookEntityExample::class.java
                )
            )
        ).thenReturn(List.of<BookEntity?>(book(1L, 2L)))

        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
    }

    @Test
    fun uniqueIsbnValidateThrowsWhenIsbnBelongsToAnotherBook() {
        Mockito.`when`<MutableList<BookEntity?>?>(
            bookMapper!!.selectByExample(
                ArgumentMatchers.any<BookEntityExample?>(
                    BookEntityExample::class.java
                )
            )
        ).thenReturn(List.of<BookEntity?>(book(2L, 1L)))

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
            .isInstanceOf(UniqueConstraintValidationException::class.java)
            .hasMessage("一意制約に違反しています: book(isbn=9784000000001)")
    }

    private fun book(id: Long?, version: Long?): BookEntity {
        val book = BookEntity()
        book.setId(id)
        book.setVersion(version)
        return book
    }
}
