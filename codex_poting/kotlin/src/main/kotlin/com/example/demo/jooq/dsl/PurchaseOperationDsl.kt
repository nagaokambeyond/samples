package com.example.demo.jooq.dsl

import com.example.demo.api.request.PurchaseInvoiceCreateRequest
import com.example.demo.api.request.PurchaseInvoiceDetailCreateRequest
import com.example.demo.data.domain.BookStockMovementSourceType
import com.example.demo.data.domain.BookStockMovementType
import com.example.demo.data.domain.PurchaseInvoiceType
import com.example.demo.jooq.entity.PurchaseInvoiceDetailRow
import com.example.demo.jooq.entity.PurchaseInvoiceRow
import com.example.demo.jooq.generated.Tables
import com.example.demo.jooq.generated.tables.records.BookStockMovementRecord
import com.example.demo.jooq.generated.tables.records.BookStockRecord
import com.example.demo.jooq.generated.tables.records.PurchaseInvoiceDetailRecord
import com.example.demo.jooq.generated.tables.records.PurchaseInvoiceRecord
import org.jooq.DSLContext
import org.jooq.Record3
import org.jooq.exception.DataAccessException
import org.springframework.context.annotation.Profile
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier

@Component
@Profile("jooq")
class PurchaseOperationDsl(private val dsl: DSLContext) {
    fun insertPurchaseInvoice(
        request: PurchaseInvoiceCreateRequest,
        amount: Long,
        now: LocalDateTime?
    ): PurchaseInvoiceRow {
        val id = dsl.insertInto<PurchaseInvoiceRecord?>(Tables.PURCHASE_INVOICE)
            .set<Int?>(Tables.PURCHASE_INVOICE.PURCHASE_INVOICE_TYPE, PurchaseInvoiceType.PURCHASE.value)
            .set<Long?>(Tables.PURCHASE_INVOICE.RETURN_PURCHASE_INVOICE_ID, null as Long?)
            .set<LocalDate?>(Tables.PURCHASE_INVOICE.PURCHASE_INVOICE_DATE, request.purchaseInvoiceDate)
            .set<Long?>(Tables.PURCHASE_INVOICE.SUPPLIER_ID, request.supplierId)
            .set<Long?>(Tables.PURCHASE_INVOICE.RECEIVING_STORE_ID, request.receivingStoreId)
            .set<Long?>(Tables.PURCHASE_INVOICE.PURCHASE_INVOICE_AMOUNT, amount)
            .set<LocalDateTime?>(Tables.PURCHASE_INVOICE.CREATE_AT, now)
            .set<LocalDateTime?>(Tables.PURCHASE_INVOICE.UPDATE_AT, now).set<Long?>(Tables.PURCHASE_INVOICE.VERSION, 1L)
            .returningResult<Long?>(Tables.PURCHASE_INVOICE.ID).fetchOne<Long?>(Tables.PURCHASE_INVOICE.ID)
        val purchaseInvoiceId = Objects.requireNonNull<Long>(id)
        return PurchaseInvoiceRow(
            purchaseInvoiceId,
            PurchaseInvoiceType.PURCHASE.value,
            null,
            request.purchaseInvoiceDate,
            request.supplierId,
            request.receivingStoreId,
            amount,
            now,
            1L
        )
    }

    fun insertPurchaseInvoiceDetail(
        purchaseInvoiceId: Long?,
        request: PurchaseInvoiceDetailCreateRequest,
        bookId: Long?,
        detailAmount: Long,
        now: LocalDateTime?
    ): PurchaseInvoiceDetailRow {
        val id = dsl.insertInto<PurchaseInvoiceDetailRecord?>(Tables.PURCHASE_INVOICE_DETAIL)
            .set<Long?>(Tables.PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_ID, purchaseInvoiceId)
            .set<Long?>(Tables.PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_BOOK_ID, bookId).set<Int?>(
                Tables.PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_UNIT_PRICE,
                request.purchaseInvoiceDetailUnitPrice
            ).set<Int?>(
                Tables.PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_QUANTITY,
                request.purchaseInvoiceDetailQuantity
            ).set<Long?>(Tables.PURCHASE_INVOICE_DETAIL.PURCHASE_INVOICE_DETAIL_AMOUNT, detailAmount)
            .set<LocalDateTime?>(Tables.PURCHASE_INVOICE_DETAIL.CREATE_AT, now)
            .set<LocalDateTime?>(Tables.PURCHASE_INVOICE_DETAIL.UPDATE_AT, now)
            .set<Long?>(Tables.PURCHASE_INVOICE_DETAIL.VERSION, 1L)
            .returningResult<Long?>(Tables.PURCHASE_INVOICE_DETAIL.ID)
            .fetchOne<Long?>(Tables.PURCHASE_INVOICE_DETAIL.ID)
        val detailId = Objects.requireNonNull<Long>(id)
        return PurchaseInvoiceDetailRow(
            detailId,
            purchaseInvoiceId,
            bookId,
            request.purchaseInvoiceDetailUnitPrice,
            request.purchaseInvoiceDetailQuantity,
            detailAmount,
            1L
        )
    }

    fun addStockQuantity(storeId: Long?, detail: PurchaseInvoiceDetailRow, now: LocalDateTime?) {
        val bookStock = selectBookStockForUpdate(storeId, detail.purchaseInvoiceDetailBookId)
        if (Objects.isNull(bookStock)) {
            dsl.insertInto<BookStockRecord?>(Tables.BOOK_STOCK)
                .set(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID, storeId)
                .set(Tables.BOOK_STOCK.BOOK_STOCK_BOOK_ID, detail.purchaseInvoiceDetailBookId)
                .set(Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY, detail.purchaseInvoiceDetailQuantity)
                .set(Tables.BOOK_STOCK.CREATE_AT, now)
                .set(Tables.BOOK_STOCK.UPDATE_AT, now).set(Tables.BOOK_STOCK.VERSION, 1L)
                .execute()
            return
        }
        val quantity =
            bookStock!!.get<Int?>(Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY)!! + detail.purchaseInvoiceDetailQuantity!!
        val version = bookStock.get<Long>(Tables.BOOK_STOCK.VERSION)
        dsl.update<BookStockRecord?>(Tables.BOOK_STOCK).set(Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY, quantity)
            .set(Tables.BOOK_STOCK.UPDATE_AT, now).set(Tables.BOOK_STOCK.VERSION, version + 1)
            .where(Tables.BOOK_STOCK.ID.eq(bookStock.get<Long?>(Tables.BOOK_STOCK.ID))).execute()
    }

    private fun selectBookStockForUpdate(storeId: Long?, bookId: Long?): Record3<Long?, Int?, Long?>? {
        return executeWithLockException<Record3<Long?, Int?, Long?>?>(Supplier {
            dsl.select<Long?, Int?, Long?>(
                Tables.BOOK_STOCK.ID,
                Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY,
                Tables.BOOK_STOCK.VERSION
            ).from(Tables.BOOK_STOCK).where(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(storeId))
                .and(Tables.BOOK_STOCK.BOOK_STOCK_BOOK_ID.eq(bookId)).forUpdate().noWait().fetchOne()
        })
    }

    private fun <T> executeWithLockException(supplier: Supplier<T?>): T? {
        try {
            return supplier.get()
        } catch (ex: DataAccessException) {
            throw PessimisticLockingFailureException("jOOQ write lock could not be acquired", ex)
        }
    }

    fun insertBookStockMovement(
        storeId: Long?,
        purchaseInvoice: PurchaseInvoiceRow,
        detail: PurchaseInvoiceDetailRow,
        now: LocalDateTime?
    ) {
        dsl.insertInto<BookStockMovementRecord?>(Tables.BOOK_STOCK_MOVEMENT)
            .set(Tables.BOOK_STOCK_MOVEMENT.STORE_ID, storeId)
            .set(Tables.BOOK_STOCK_MOVEMENT.BOOK_ID, detail.purchaseInvoiceDetailBookId)
            .set(Tables.BOOK_STOCK_MOVEMENT.MOVEMENT_TYPE, BookStockMovementType.PURCHASE.value)
            .set(Tables.BOOK_STOCK_MOVEMENT.QUANTITY_DELTA, detail.purchaseInvoiceDetailQuantity)
            .set(Tables.BOOK_STOCK_MOVEMENT.SOURCE_TYPE, BookStockMovementSourceType.PURCHASE_INVOICE.value)
            .set(Tables.BOOK_STOCK_MOVEMENT.SOURCE_ID, purchaseInvoice.id)
            .set(Tables.BOOK_STOCK_MOVEMENT.SOURCE_DETAIL_ID, detail.id)
            .set(Tables.BOOK_STOCK_MOVEMENT.MOVEMENT_DATE, purchaseInvoice.purchaseInvoiceDate)
            .set(Tables.BOOK_STOCK_MOVEMENT.CREATE_AT, now)
            .set(Tables.BOOK_STOCK_MOVEMENT.UPDATE_AT, now)
            .set(Tables.BOOK_STOCK_MOVEMENT.VERSION, 1L).execute()
    }
}
