package com.example.demo.jooq.dsl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.example.demo.jooq.generated.Tables.BOOK_GENRE;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class BookGenreDsl {
    private final DSLContext dsl;

    public boolean exists(Long genreId) {
        return dsl.fetchExists(BOOK_GENRE, BOOK_GENRE.ID.eq(genreId));
    }
}
