package com.example.demo.jooq.dsl

import com.example.demo.jooq.generated.Tables
import org.jooq.DSLContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("jooq")
class BookGenreDsl(private val dsl: DSLContext) {
    fun exists(genreId: Long?): Boolean {
        return dsl.fetchExists(Tables.BOOK_GENRE, Tables.BOOK_GENRE.ID.eq(genreId))
    }
}
