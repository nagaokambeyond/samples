package com.example.demo.jooq.dsl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.example.demo.jooq.generated.Tables.STORE;

@Component
@Profile("jooq")
@RequiredArgsConstructor
public class StoreDsl {
    private final DSLContext dsl;

    public boolean exists(Long storeId) {
        return dsl.fetchExists(STORE, STORE.ID.eq(storeId));
    }
}
