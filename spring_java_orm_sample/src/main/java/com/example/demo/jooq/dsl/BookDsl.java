package com.example.demo.jooq.dsl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.example.demo.jooq.generated.Tables.BOOK;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class BookDsl {
    private final DSLContext dsl;

    public boolean exists(Long bookId) {
        return dsl.fetchExists(BOOK, BOOK.ID.eq(bookId));
    }
}
