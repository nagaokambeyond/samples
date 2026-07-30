package com.example.demo.mybatis.service

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.api.response.PurchaseInvoiceDetailResponse
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.exception.ForeignKeyReferenceNotFoundException
import com.example.demo.mybatis.generator.mapper.BookStockMapper
import com.example.demo.mybatis.generator.mapper.PurchaseOrderDetailMapper
import com.example.demo.mybatis.generator.mapper.PurchaseOrderMapper
import com.example.demo.service.PurchaseOperationService
import org.assertj.core.api.Assertions
import org.assertj.core.api.ThrowableAssert.ThrowingCallable
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.lang.AutoCloseable
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.time.LocalDate
import java.util.List
import java.util.function.Consumer
import javax.sql.DataSource

@SpringBootTest
@ActiveProfiles("mybatis")
@Transactional
internal class PurchaseOperationServiceMybatisTest {
    @Autowired
    private val purchaseOperationService: PurchaseOperationService? = null

    @Autowired
    private val purchaseOperationServiceMybatis: PurchaseOperationServiceMybatis? = null

    @Autowired
    private val purchaseOrderMapper: PurchaseOrderMapper? = null

    @Autowired
    private val purchaseOrderDetailMapper: PurchaseOrderDetailMapper? = null

    @Autowired
    private val bookStockMapper: BookStockMapper? = null

    @Autowired
    private val jdbcTemplate: JdbcTemplate? = null

    @Autowired
    private val dataSource: DataSource? = null

    @Test
    fun usesMybatisAsPrimaryPurchaseService() {
        Assertions.assertThat(purchaseOperationService)
            .isInstanceOf(PurchaseOperationServiceMybatis::class.java)
        Assertions.assertThat(purchaseOperationServiceMybatis).isNotNull()
    }

    @Test
    fun createReturnsResponseAndPersistsPurchaseInvoiceDetailsAndAddsExistingStock() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of<PurchaseInvoiceDetailCreateRequest>(
                PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2),
                PurchaseInvoiceDetailCreateRequest("0000000000002", 500, 3)
            )
        )

        val response = purchaseOperationServiceMybatis!!.create(request)

        Assertions.assertThat(response!!.id).isNotNull()
        Assertions.assertThat(response.purchaseInvoiceType)
            .isEqualTo(PurchaseInvoiceType.PURCHASE)
        Assertions.assertThat(response.returnPurchaseInvoiceId).isNull()
        Assertions.assertThat(response.purchaseInvoiceDate).isEqualTo(LocalDate.of(2026, 2, 1))
        Assertions.assertThat(response.supplierId).isEqualTo(1L)
        Assertions.assertThat(response.receivingStoreId).isEqualTo(1L)
        Assertions.assertThat(response.purchaseInvoiceAmount).isEqualTo(3500L)
        Assertions.assertThat(response.updateAt).isNotNull()
        Assertions.assertThat(response.version).isEqualTo(1L)
        Assertions.assertThat(response.detail)
            .extracting(
                "purchaseInvoiceId",
                "purchaseInvoiceDetailBookId",
                "purchaseInvoiceDetailUnitPrice",
                "purchaseInvoiceDetailQuantity",
                "purchaseInvoiceDetailAmount",
                "version"
            )
            .containsExactly(
                Assertions.tuple(response.id, 1L, 1000, 2, 2000L, 1L),
                Assertions.tuple(response.id, 2L, 500, 3, 1500L, 1L)
            )

        val purchaseInvoice = purchaseOrderMapper!!.selectByPrimaryKey(response.id)
        Assertions.assertThat(purchaseInvoice.getPurchaseInvoiceType())
            .isEqualTo(PurchaseInvoiceType.PURCHASE)
        Assertions.assertThat(purchaseInvoice.getPurchaseInvoiceAmount()).isEqualTo(3500L)

        response.detail!!.forEach(Consumer { detail: PurchaseInvoiceDetailResponse? ->
            val persistedDetail = purchaseOrderDetailMapper!!.selectByPrimaryKey(detail!!.id)
            Assertions.assertThat(persistedDetail.getPurchaseInvoiceId()).isEqualTo(response.id)
            Assertions.assertThat(persistedDetail.getPurchaseInvoiceDetailAmount())
                .isEqualTo(detail.purchaseInvoiceDetailAmount)
        })
        Assertions.assertThat(bookStockMapper!!.selectByPrimaryKey(1L).getBookStockQuantity()).isEqualTo(12)
        Assertions.assertThat(bookStockMapper.selectByPrimaryKey(4L).getBookStockQuantity()).isEqualTo(14)
        assertBookStockMovements(
            response.id,
            List.of<Tuple?>(
                Assertions.tuple(
                    1L,
                    1L,
                    2,
                    2,
                    1,
                    response.id,
                    response.detail!!.get(0).id,
                    LocalDate.of(2026, 2, 1),
                    1L
                ),
                Assertions.tuple(
                    1L,
                    2L,
                    2,
                    3,
                    1,
                    response.id,
                    response.detail!!.get(1).id,
                    LocalDate.of(2026, 2, 1),
                    1L
                )
            )
        )
    }

    @Test
    fun createInsertsBookStockWhenStockDoesNotExist() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 2),
            1L,
            1L,
            List.of<PurchaseInvoiceDetailCreateRequest>(PurchaseInvoiceDetailCreateRequest("0000000000006", 800, 4))
        )

        val response = purchaseOperationServiceMybatis!!.create(request)

        val stockId: Long? = jdbcTemplate!!.queryForObject(
            """
                select id
                from book_stock
                where book_stock_store_id = ? and book_stock_book_id = ?
                
                """.trimIndent(),
            Long::class.javaObjectType,
            1L,
            6L
        )
        val bookStock = bookStockMapper!!.selectByPrimaryKey(stockId)
        Assertions.assertThat(bookStock.getBookStockQuantity()).isEqualTo(4)
        Assertions.assertThat(bookStock.getVersion()).isEqualTo(1L)
        assertBookStockMovements(
            response!!.id,
            List.of<Tuple?>(
                Assertions.tuple(
                    1L,
                    6L,
                    2,
                    4,
                    1,
                    response.id,
                    response.detail!!.first().id,
                    LocalDate.of(2026, 2, 2),
                    1L
                )
            )
        )
    }

    @Test
    fun createThrowsWhenSupplierDoesNotExist() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            999L,
            1L,
            List.of<PurchaseInvoiceDetailCreateRequest>(PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
        )

        Assertions.assertThatThrownBy(ThrowingCallable { purchaseOperationServiceMybatis!!.create(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: supplier(id=999)")
    }

    @Test
    fun createThrowsWhenReceivingStoreDoesNotExist() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            999L,
            List.of<PurchaseInvoiceDetailCreateRequest>(PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
        )

        Assertions.assertThatThrownBy(ThrowingCallable { purchaseOperationServiceMybatis!!.create(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: store(id=999)")
    }

    @Test
    fun createThrowsWhenBookDoesNotExist() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of<PurchaseInvoiceDetailCreateRequest>(PurchaseInvoiceDetailCreateRequest("0000000000999", 1000, 2))
        )

        Assertions.assertThatThrownBy(ThrowingCallable { purchaseOperationServiceMybatis!!.create(request) })
            .isInstanceOf(ForeignKeyReferenceNotFoundException::class.java)
            .hasMessage("参照先データが存在しません: book(isbn=0000000000999)")
    }

    @Test
    @Throws(Exception::class)
    fun createThrowsWhenExistingBookStockWriteLockCannotBeAcquired() {
        val request = PurchaseInvoiceCreateRequest(
            LocalDate.of(2026, 2, 1),
            1L,
            1L,
            List.of<PurchaseInvoiceDetailCreateRequest>(PurchaseInvoiceDetailCreateRequest("0000000000001", 1000, 2))
        )

        BookStockRowLock.acquire(dataSource!!, 1L, 1L).use { ignored ->
            Assertions.assertThatThrownBy(ThrowingCallable { purchaseOperationServiceMybatis!!.create(request) })
                .isInstanceOf(PessimisticLockingFailureException::class.java)
        }
    }

    private class BookStockRowLock(
        private val connection: Connection,
        private val statement: PreparedStatement,
        private val resultSet: ResultSet
    ) : AutoCloseable {
        @Throws(SQLException::class)
        override fun close() {
            try {
                resultSet.close()
                statement.close()
            } finally {
                connection.rollback()
                connection.close()
            }
        }

        companion object {
            @Throws(SQLException::class)
            fun acquire(dataSource: DataSource, storeId: Long, bookId: Long): BookStockRowLock {
                val connection = dataSource.getConnection()
                connection.setAutoCommit(false)
                val statement = connection.prepareStatement(
                    "select id from book_stock where book_stock_store_id = ? and book_stock_book_id = ? for update"
                )
                statement.setLong(1, storeId)
                statement.setLong(2, bookId)
                val resultSet = statement.executeQuery()
                resultSet.next()
                return BookStockRowLock(connection, statement, resultSet)
            }
        }
    }

    private fun assertBookStockMovements(sourceId: Long?, expected: MutableList<Tuple?>?) {
        val movements = jdbcTemplate!!.query<Tuple?>(
            """
                select store_id, book_id, movement_type, quantity_delta, source_type, source_id, source_detail_id, movement_date, version
                from book_stock_movement
                where source_id = ?
                order by source_detail_id
                
                """.trimIndent(),
            RowMapper { rs: ResultSet?, rowNum: Int ->
                Assertions.tuple(
                    rs!!.getLong("store_id"),
                    rs.getLong("book_id"),
                    rs.getInt("movement_type"),
                    rs.getInt("quantity_delta"),
                    rs.getInt("source_type"),
                    rs.getLong("source_id"),
                    rs.getLong("source_detail_id"),
                    rs.getObject<LocalDate?>("movement_date", LocalDate::class.java),
                    rs.getLong("version")
                )
            },
            sourceId
        )
        Assertions.assertThat(movements).containsExactlyElementsOf(expected)
    }
}
