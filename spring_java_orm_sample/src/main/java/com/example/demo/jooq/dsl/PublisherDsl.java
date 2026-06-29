package com.example.demo.jooq.dsl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.example.demo.jooq.generated.Tables.PUBLISHER;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class PublisherDsl {
    private final DSLContext dsl;

    public boolean existsPublisher(Long publisherId) {
        return dsl.fetchExists(PUBLISHER, PUBLISHER.ID.eq(publisherId));
    }
}
