package com.example.demo.jooq.dsl

import com.example.demo.jooq.generated.Tables
import org.jooq.DSLContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class BookDsl(private val dsl: DSLContext) {
    fun exists(bookId: Long?): Boolean {
        return dsl.fetchExists(Tables.BOOK, Tables.BOOK.ID.eq(bookId))
    }

    fun selectIdByIsbn(isbn: String?): Long? {
        return dsl.select<Long?>(Tables.BOOK.ID).from(Tables.BOOK).where(Tables.BOOK.ISBN.eq(isbn))
            .fetchOne<Long?>(Tables.BOOK.ID)
    }
}
