package com.example.demo.jooq.validator

import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.exception.UniqueConstraintValidationException
import com.example.demo.jooq.dsl.BookDsl
import com.example.demo.jooq.dsl.BookGenreDsl
import com.example.demo.jooq.dsl.PublisherDsl
import com.example.demo.jooq.entity.BookWithStockRow
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.orm.ObjectOptimisticLockingFailureException

internal class BookDataValidatorJooqTest {
    private var bookDsl: BookDsl? = null
    private var publisherDsl: PublisherDsl? = null
    private var bookGenreDsl: BookGenreDsl? = null
    private var validator: BookDataValidatorJooq? = null

    @BeforeEach
    fun setUp() {
        bookDsl = Mockito.mock<BookDsl>(BookDsl::class.java)
        publisherDsl = Mockito.mock<PublisherDsl>(PublisherDsl::class.java)
        bookGenreDsl = Mockito.mock<BookGenreDsl>(BookGenreDsl::class.java)
        validator = BookDataValidatorJooq(bookDsl!!, publisherDsl!!, bookGenreDsl!!)
    }

    @Test
    fun foreignKeyValidateAllowsExistingPublisherAndGenre() {
        Mockito.`when`<Boolean?>(publisherDsl!!.existsPublisher(1L)).thenReturn(true)
        Mockito.`when`<Boolean?>(bookGenreDsl!!.exists(5L)).thenReturn(true)

        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(1L, 5L) })
    }

    @Test
    fun foreignKeyValidateThrowsWhenPublisherDoesNotExist() {
        Mockito.`when`<Boolean?>(publisherDsl!!.existsPublisher(999L)).thenReturn(false)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(999L, 5L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: publisher(id=999)")
        Mockito.verifyNoInteractions(bookGenreDsl)
    }

    @Test
    fun foreignKeyValidateThrowsWhenBookGenreDoesNotExist() {
        Mockito.`when`<Boolean?>(publisherDsl!!.existsPublisher(1L)).thenReturn(true)
        Mockito.`when`<Boolean?>(bookGenreDsl!!.exists(999L)).thenReturn(false)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.foreignKeyValidate(1L, 999L) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book_genre(id=999)")
    }

    @Test
    fun versionValidateAllowsMatchingVersion() {
        Assertions.assertThatNoException().isThrownBy(ThrowingCallable { validator!!.versionValidate(1L, 2L, 2L) })
    }

    @Test
    fun versionValidateThrowsWhenVersionIsStale() {
        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.versionValidate(1L, 2L, 1L) })
            .isInstanceOf(ObjectOptimisticLockingFailureException::class.java)
            .satisfies({ exception: Throwable? ->
                Assertions.assertThat((exception as ObjectOptimisticLockingFailureException).getPersistentClass())
                    .isEqualTo(BookWithStockRow::class.java)
            })
    }

    @Test
    fun uniqueIsbnValidateAllowsUnusedIsbn() {
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("9784000000001")).thenReturn(null)

        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
    }

    @Test
    fun uniqueIsbnValidateAllowsKeepingCurrentBookIsbn() {
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("9784000000001")).thenReturn(1L)

        Assertions.assertThatNoException()
            .isThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
    }

    @Test
    fun uniqueIsbnValidateThrowsWhenIsbnBelongsToAnotherBook() {
        Mockito.`when`<Long?>(bookDsl!!.selectIdByIsbn("9784000000001")).thenReturn(2L)

        Assertions.assertThatThrownBy(ThrowingCallable { validator!!.uniqueIsbnValidate("9784000000001", 1L) })
            .isInstanceOf(UniqueConstraintValidationException::class.java)
            .hasMessage("一意制約に違反しています: book(isbn=9784000000001)")
    }
}
