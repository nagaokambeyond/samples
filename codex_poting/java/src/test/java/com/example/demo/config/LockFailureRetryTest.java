package com.example.demo.config;

import com.example.demo.BookRowLock;
import com.example.demo.api.request.BookUpdateRequest;
import com.example.demo.doma.service.BooksOperationServiceDoma;
import com.example.demo.jpa.service.BooksOperationServiceJPA;
import com.example.demo.mybatis.service.BooksOperationServiceMybatis;
import com.example.demo.service.BooksOperationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.resilience.retry.MethodRetryEvent;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles({"doma", "jpa", "mybatis"})
class LockFailureRetryTest {
    @Autowired
    private BooksOperationServiceJPA jpaBookService;

    @Autowired
    private BooksOperationServiceMybatis mybatisBookService;

    @Autowired
    private BooksOperationServiceDoma domaBookService;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RetryEventRecorder retryEventRecorder;

    @BeforeEach
    void setUp() {
        retryEventRecorder.clear();
    }

    @Test
    void jpaUpdateRetriesWhenWriteLockCannotBeAcquired() throws Exception {
        assertUpdateRetriesOnLockFailure(jpaBookService);
    }

    @Test
    void mybatisUpdateRetriesWhenWriteLockCannotBeAcquired() throws Exception {
        assertUpdateRetriesOnLockFailure(mybatisBookService);
    }

    @Test
    void domaUpdateRetriesWhenWriteLockCannotBeAcquired() throws Exception {
        assertUpdateRetriesOnLockFailure(domaBookService);
    }

    private void assertUpdateRetriesOnLockFailure(BooksOperationService booksOperationService) throws Exception {
        final var before = booksOperationService.findById(1L);

        try (final var ignored = BookRowLock.acquire(dataSource, 1L)) {
            assertThatThrownBy(() -> booksOperationService.update(new BookUpdateRequest(1L, "ロック失敗", "Saburo", LocalDate.of(2021, 2, 1), 1L, 5L, "9784000000901", before.getVersion())))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }

        final var retryFailures = retryEventRecorder.events().stream()
            .filter(event -> !event.isRetryAborted())
            .filter(event -> event.getMethod().getName().equals("update"))
            .count();
        assertThat(retryFailures).isGreaterThanOrEqualTo(2);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        RetryEventRecorder retryEventRecorder() {
            return new RetryEventRecorder();
        }
    }

    static class RetryEventRecorder {
        private final List<MethodRetryEvent> events = new CopyOnWriteArrayList<>();

        @EventListener
        void record(MethodRetryEvent event) {
            events.add(event);
        }

        List<MethodRetryEvent> events() {
            return events;
        }

        void clear() {
            events.clear();
        }
    }
}
