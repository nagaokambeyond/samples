package com.example.demo.doma.service;

import com.example.demo.api.request.PurchaseInvoiceCreateRequest;
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest;
import com.example.demo.data.domain.PurchaseInvoiceType;
import com.example.demo.doma.generator.dao.BookStockDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDao;
import com.example.demo.doma.generator.dao.PurchaseInvoiceDetailDao;
import com.example.demo.exception.ForeignKeyReferenceNotFoundException;
import com.example.demo.service.PurchaseOperationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("doma")
@Transactional
class PurchaseOperationServiceDomaTest {
    @Autowired
    private PurchaseOperationService purchaseOperationService;

    @Autowired
    private PurchaseInvoiceDao purchaseInvoiceDao;

    @Autowired
    private PurchaseInvoiceDetailDao purchaseInvoiceDetailDao;

    @Autowired
    private BookStockDao bookStockDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Test
    void usesDomaAsPrimaryPurchaseService() {
        assertThat(purchaseOperationService).isInstanceOf(PurchaseOperationServiceDoma.class);
    }

    @Test
    void createReturnsResponseAndPersistsPurchaseInvoiceDetailsAndAddsExistingStock() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of(
                new PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2),
                new PurchaseInvoiceDetailCreateRequest("0000000000002", 500, 3)
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

        final var purchaseInvoice = purchaseInvoiceDao.selectById(response.getId());
        assertThat(purchaseInvoice.getPurchaseInvoiceType()).isEqualTo(PurchaseInvoiceType.PURCHASE);
        assertThat(purchaseInvoice.getPurchaseInvoiceAmount()).isEqualTo(3500L);

        response.getDetail().forEach(detail -> {
            final var persistedDetail = purchaseInvoiceDetailDao.selectById(detail.getId());
            assertThat(persistedDetail.getPurchaseInvoiceId()).isEqualTo(response.getId());
            assertThat(persistedDetail.getPurchaseInvoiceDetailAmount()).isEqualTo(detail.getPurchaseInvoiceDetailAmount());
        });
        assertThat(bookStockDao.selectById(1L).getBookStockQuantity()).isEqualTo(12);
        assertThat(bookStockDao.selectById(4L).getBookStockQuantity()).isEqualTo(14);
    }

    @Test
    void createInsertsBookStockWhenStockDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 2),
            1L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest("0000000000006", 800, 4))
        );

        purchaseOperationService.create(request);

        final var stockId = jdbcTemplate.queryForObject(
            """
                select id
                from book_stock
                where book_stock_store_id = ? and book_stock_book_id = ?
                """,
            Long.class,
            1L,
            6L
        );
        final var bookStock = bookStockDao.selectById(stockId);
        assertThat(bookStock.getBookStockQuantity()).isEqualTo(4);
        assertThat(bookStock.getVersion()).isEqualTo(1L);
    }

    @Test
    void createThrowsWhenSupplierDoesNotExist() {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            999L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
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
            List.of(new PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
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
            List.of(new PurchaseInvoiceDetailCreateRequest("0000000000999", 1000, 2))
        );

        assertThatThrownBy(() -> purchaseOperationService.create(request))
            .isInstanceOf(ForeignKeyReferenceNotFoundException.class)
            .hasMessage("参照先データが存在しません: book(isbn=0000000000999)");
    }

    @Test
    void createThrowsWhenExistingBookStockWriteLockCannotBeAcquired() throws Exception {
        final var request = new PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of(new PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
        );

        try (final var ignored = BookStockRowLock.acquire(dataSource, 1L, 1L)) {
            assertThatThrownBy(() -> purchaseOperationService.create(request))
                .isInstanceOf(PessimisticLockingFailureException.class);
        }
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
