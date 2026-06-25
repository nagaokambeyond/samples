package com.example.demo.jooq.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.service.PurchaseOperationServiceDoma;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.service.PurchaseOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@Transactional
class PurchaseOperationServiceJooqTest {
    @Autowired
    private PurchaseOperationService primaryPurchaseOperationService;

    @Autowired
    private PurchaseOperationServiceJooq purchaseOperationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    void keepsDomaAsPrimaryPurchaseService() {
        assertThat(primaryPurchaseOperationService).isInstanceOf(PurchaseOperationServiceDoma.class);
    }

    @Test
    void createReturnsResponseAndPersistsPurchaseInvoiceDetailsAndAddsExistingStock() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of(
                new PurchaseInvoiceDetailCreateRequest(1L, 1000, 2),
                new PurchaseInvoiceDetailCreateRequest(2L, 500, 3)
            )
        );

        final var response = purchaseOperationService.create(request);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getPurchaseInvoiceType()).isEqualTo(PurchaseInvoiceType.PURCHASE);
        assertThat(response.getReturnPurchaseInvoiceId()).isNull();
        assertThat(response.getPurchaseInvoiceDate()).isEqualTo(LocalDate.of(2026, 2, 1));
        assertThat(response.getSupplierId()).isEqualTo(1L);
        assertThat(response.getReceivingStoreId()).isEqualTo(1L);
        assertThat(response.getPurchaseInvoiceAmount()).isEqualTo(3500L);
        assertThat(response.getUpdateAt()).isNotNull();
        assertThat(response.getVersion()).isEqualTo(1L);
        assertThat(response.getDetail())
            .extracting(
                "purchaseInvoiceId",
                "purchaseInvoiceDetailBookId",
                "purchaseInvoiceDetailUnitPrice",
                "purchaseInvoiceDetailQuantity",
                "purchaseInvoiceDetailAmount",
                "version"
            )
            .containsExactly(
                tuple(response.getId(), 1L, 1000, 2, 2000L, 1L),
                tuple(response.getId(), 2L, 500, 3, 1500L, 1L)
            );

        assertThat(selectLong("select purchase_invoice_amount from purchase_invoice where id = ?", response.getId()))
            .isEqualTo(3500L);
        assertThat(selectInt("select book_stock_quantity from book_stock where id = ?", 1L)).isEqualTo(12);
        assertThat(selectInt("select book_stock_quantity from book_stock where id = ?", 4L)).isEqualTo(14);
    }

    @Test
    void createInsertsBookStockWhenStockDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 2),
            1L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest(6L, 800, 4))
        );

        purchaseOperationService.create(request);

        final var quantity = selectInt(
            """
                select book_stock_quantity
                from book_stock
                where book_stock_store_id = ? and book_stock_book_id = ?
                """,
            1L,
            6L
        );
        assertThat(quantity).isEqualTo(4);
    }

    @Test
    void createThrowsWhenSupplierDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            999L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest(1L, 1000, 2))
        );

        assertThatThrownBy(() -> purchaseOperationService.create(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: supplier(id=999)");
    }

    @Test
    void createThrowsWhenReceivingStoreDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            999L,
            List.of(new PurchaseInvoiceDetailCreateRequest(1L, 1000, 2))
        );

        assertThatThrownBy(() -> purchaseOperationService.create(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: store(id=999)");
    }

    @Test
    void createThrowsWhenBookDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest(999L, 1000, 2))
        );

        assertThatThrownBy(() -> purchaseOperationService.create(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book(id=999)");
    }

    @Test
    void createThrowsWhenExistingBookStockWriteLockCannotBeAcquired() throws Exception {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest(1L, 1000, 2))
        );

        try (final var ignored = BookStockRowLock.acquire(dataSource, 1L, 1L)) {
            assertThatThrownBy(() -> purchaseOperationService.create(request))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }
    }

    private Long selectLong(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, Long.class, args);
    }

    private Integer selectInt(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, Integer.class, args);
    }

    private static class BookStockRowLock implements AutoCloseable {
        private final Connection connection;
        private final PreparedStatement statement;
        private final ResultSet resultSet;

        private BookStockRowLock(Connection connection, PreparedStatement statement, ResultSet resultSet) {
            this.connection = connection;
            this.statement = statement;
            this.resultSet = resultSet;
        }

        private static BookStockRowLock acquire(DataSource dataSource, Long storeId, Long bookId) throws SQLException {
            final var connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            final var statement = connection.prepareStatement(
                "select id from book_stock where book_stock_store_id = ? and book_stock_book_id = ? for update"
            );
            statement.setLong(1, storeId);
            statement.setLong(2, bookId);
            final var resultSet = statement.executeQuery();
            resultSet.next();
            return new BookStockRowLock(connection, statement, resultSet);
        }

        @Override
        public void close() throws SQLException {
            try {
                resultSet.close();
                statement.close();
            } finally {
                connection.rollback();
                connection.close();
            }
        }
    }
}
