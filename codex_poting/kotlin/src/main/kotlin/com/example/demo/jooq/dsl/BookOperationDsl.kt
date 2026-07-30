package com.example.demo.jooq.dsl

import com.example.demo.api.request.BookCreateRequest
import com.example.demo.api.request.BookUpdateRequest
import com.example.demo.jooq.entity.BookSalesUnitPriceHistoryRow
import com.example.demo.jooq.entity.BookWithStockRow
import com.example.demo.jooq.generated.Tables
import com.example.demo.jooq.generated.tables.records.BookRecord
import com.example.demo.jooq.generated.tables.records.BookSalesUnitPriceHistoryRecord
import org.jooq.*
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.springframework.context.annotation.Profile
import org.springframework.dao.PessimisticLockingFailureException
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier

@Component
@Profile("jooq")
class BookOperationDsl(private val dsl: DSLContext) {
    fun selectBookForUpdate(id: Long?): Record2<Long?, Long?>? {
        return executeWithLockException<Record2<Long?, Long?>?>(Supplier {
            dsl.select<Long?, Long?>(
                Tables.BOOK.ID,
                Tables.BOOK.VERSION
            ).from(Tables.BOOK).where(Tables.BOOK.ID.eq(id)).forUpdate().noWait().fetchOne()
        })
    }

    fun selectByIdWithPublisherName(id: Long?): MutableList<BookWithStockRow?> {
        return dsl.select<Long?, String?, String?, LocalDate?, Long?, String?, Long?, String?, String?, Int?, LocalDateTime?, Long?, Long?, Long?, String?, Int?>(
            Tables.BOOK.ID,
            Tables.BOOK.TITLE,
            Tables.BOOK.AUTHOR,
            Tables.BOOK.RELEASE_DATE,
            Tables.BOOK.PUBLISHER_ID,
            Tables.PUBLISHER.PUBLISHER_NAME,
            Tables.BOOK.GENRE_ID,
            Tables.BOOK_GENRE.GENRE_NAME,
            Tables.BOOK.ISBN,
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE,
            Tables.BOOK.UPDATE_AT,
            Tables.BOOK.VERSION,
            Tables.BOOK_STOCK.ID,
            Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID,
            Tables.STORE.STORE_NAME,
            Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY
        ).from(Tables.BOOK).join(Tables.PUBLISHER).on(Tables.BOOK.PUBLISHER_ID.eq(Tables.PUBLISHER.ID))
            .join(Tables.BOOK_GENRE).on(Tables.BOOK.GENRE_ID.eq(Tables.BOOK_GENRE.ID))
            .join(Tables.BOOK_SALES_UNIT_PRICE_HISTORY)
            .on(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(Tables.BOOK.ID).and(currentSalesUnitPriceCondition()))
            .leftJoin(Tables.BOOK_STOCK).on(Tables.BOOK.ID.eq(Tables.BOOK_STOCK.BOOK_STOCK_BOOK_ID))
            .leftJoin(Tables.STORE).on(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(Tables.STORE.ID))
            .where(Tables.BOOK.ID.eq(id)).orderBy<Long?, Long?>(Tables.BOOK.ID, Tables.BOOK_STOCK.ID)
            .fetch<BookWithStockRow?>(RecordMapper { row: Record16<Long?, String?, String?, LocalDate?, Long?, String?, Long?, String?, String?, Int?, LocalDateTime?, Long?, Long?, Long?, String?, Int?>? ->
                BookWithStockRow(
                    row!!.get<Long?>(Tables.BOOK.ID),
                    row.get<String?>(Tables.BOOK.TITLE),
                    row.get<String?>(Tables.BOOK.AUTHOR),
                    row.get<LocalDate?>(Tables.BOOK.RELEASE_DATE),
                    row.get<Long?>(Tables.BOOK.PUBLISHER_ID),
                    row.get<String?>(Tables.PUBLISHER.PUBLISHER_NAME),
                    row.get<Long?>(Tables.BOOK.GENRE_ID),
                    row.get<String?>(Tables.BOOK_GENRE.GENRE_NAME),
                    row.get<String?>(Tables.BOOK.ISBN),
                    row.get<Int?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE),
                    row.get<LocalDateTime?>(Tables.BOOK.UPDATE_AT),
                    row.get<Long?>(Tables.BOOK.VERSION),
                    row.get<Long?>(Tables.BOOK_STOCK.ID),
                    row.get<Long?>(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID),
                    row.get<String?>(Tables.STORE.STORE_NAME),
                    row.get<Int?>(Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY)
                )
            })
    }

    fun selectByTitleOrAuthorStartingWithIgnoreCase(
        condition: Condition?,
        size: Int,
        offset: Long
    ): MutableList<BookWithStockRow?> {
        val pagedBooks =
            dsl.select<Long?, String?, String?, LocalDate?, Long?, Long?, String?, Int?, LocalDateTime?, Long?>(
                Tables.BOOK.ID,
                Tables.BOOK.TITLE,
                Tables.BOOK.AUTHOR,
                Tables.BOOK.RELEASE_DATE,
                Tables.BOOK.PUBLISHER_ID,
                Tables.BOOK.GENRE_ID,
                Tables.BOOK.ISBN,
                Tables.BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE,
                Tables.BOOK.UPDATE_AT,
                Tables.BOOK.VERSION
            ).from(Tables.BOOK).join(Tables.BOOK_SALES_UNIT_PRICE_HISTORY).on(
                Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(Tables.BOOK.ID).and(currentSalesUnitPriceCondition())
            ).where(condition).orderBy<Long?>(Tables.BOOK.ID).limit(size).offset(offset).asTable("paged_books")
        val id: Field<Long?> = Objects.requireNonNull<Field<Long?>?>(pagedBooks.field<Long?>(Tables.BOOK.ID))!!
        val title: Field<String?> =
            Objects.requireNonNull<Field<String?>?>(pagedBooks.field<String?>(Tables.BOOK.TITLE))!!
        val author: Field<String?> =
            Objects.requireNonNull<Field<String?>?>(pagedBooks.field<String?>(Tables.BOOK.AUTHOR))!!
        val releaseDate: Field<LocalDate?> =
            Objects.requireNonNull<Field<LocalDate?>?>(pagedBooks.field<LocalDate?>(Tables.BOOK.RELEASE_DATE))!!
        val publisherId: Field<Long?> =
            Objects.requireNonNull<Field<Long?>?>(pagedBooks.field<Long?>(Tables.BOOK.PUBLISHER_ID))!!
        val genreId: Field<Long?> =
            Objects.requireNonNull<Field<Long?>?>(pagedBooks.field<Long?>(Tables.BOOK.GENRE_ID))!!
        val isbn: Field<String?> =
            Objects.requireNonNull<Field<String?>?>(pagedBooks.field<String?>(Tables.BOOK.ISBN))!!
        val salesUnitPrice: Field<Int?> =
            Objects.requireNonNull<Field<Int?>?>(pagedBooks.field<Int?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE))!!
        val updateAt: Field<LocalDateTime?> =
            Objects.requireNonNull<Field<LocalDateTime?>?>(pagedBooks.field<LocalDateTime?>(Tables.BOOK.UPDATE_AT))!!
        val version: Field<Long?> =
            Objects.requireNonNull<Field<Long?>?>(pagedBooks.field<Long?>(Tables.BOOK.VERSION))!!
        return dsl.select<Long?, String?, String?, LocalDate?, Long?, String?, Long?, String?, String?, Int?, LocalDateTime?, Long?, Long?, Long?, String?, Int?>(
            id,
            title,
            author,
            releaseDate,
            publisherId,
            Tables.PUBLISHER.PUBLISHER_NAME,
            genreId,
            Tables.BOOK_GENRE.GENRE_NAME,
            isbn,
            salesUnitPrice,
            updateAt,
            version,
            Tables.BOOK_STOCK.ID,
            Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID,
            Tables.STORE.STORE_NAME,
            Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY
        ).from(pagedBooks).join(Tables.PUBLISHER).on(publisherId.eq(Tables.PUBLISHER.ID)).join(Tables.BOOK_GENRE)
            .on(genreId.eq(Tables.BOOK_GENRE.ID)).leftJoin(Tables.BOOK_STOCK)
            .on(id.eq(Tables.BOOK_STOCK.BOOK_STOCK_BOOK_ID)).leftJoin(Tables.STORE)
            .on(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID.eq(Tables.STORE.ID))
            .orderBy<Long?, Long?>(id, Tables.BOOK_STOCK.ID)
            .fetch<BookWithStockRow?>(RecordMapper { row: Record16<Long?, String?, String?, LocalDate?, Long?, String?, Long?, String?, String?, Int?, LocalDateTime?, Long?, Long?, Long?, String?, Int?>? ->
                BookWithStockRow(
                    row!!.get<Long?>(id),
                    row.get<String?>(title),
                    row.get<String?>(author),
                    row.get<LocalDate?>(releaseDate),
                    row.get<Long?>(publisherId),
                    row.get<String?>(Tables.PUBLISHER.PUBLISHER_NAME),
                    row.get<Long?>(genreId),
                    row.get<String?>(Tables.BOOK_GENRE.GENRE_NAME),
                    row.get<String?>(isbn),
                    row.get<Int?>(salesUnitPrice),
                    row.get<LocalDateTime?>(updateAt),
                    row.get<Long?>(version),
                    row.get<Long?>(Tables.BOOK_STOCK.ID),
                    row.get<Long?>(Tables.BOOK_STOCK.BOOK_STOCK_STORE_ID),
                    row.get<String?>(Tables.STORE.STORE_NAME),
                    row.get<Int?>(Tables.BOOK_STOCK.BOOK_STOCK_QUANTITY)
                )
            })
    }

    fun searchCondition(keyword: String?, releaseDateFrom: LocalDate?, releaseDateTo: LocalDate?): Condition {
        val conditions = ArrayList<Condition>()
        if (keyword != null && !keyword.isBlank()) {
            val pattern = keyword.trim { it <= ' ' }.lowercase(Locale.getDefault()) + "%"
            conditions.add(DSL.lower(Tables.BOOK.TITLE).like(pattern).or(DSL.lower(Tables.BOOK.AUTHOR).like(pattern)))
        }
        if (releaseDateFrom != null) {
            conditions.add(Tables.BOOK.RELEASE_DATE.ge(releaseDateFrom))
        }
        if (releaseDateTo != null) {
            conditions.add(Tables.BOOK.RELEASE_DATE.le(releaseDateTo))
        }
        return conditions.stream().reduce(DSL.noCondition()) { obj: Condition?, arg2: Condition? -> obj!!.and(arg2) }
    }

    fun totalElements(condition: Condition?): Int {
        return dsl.fetchCount(
            dsl.selectOne().from(Tables.BOOK).join(Tables.BOOK_SALES_UNIT_PRICE_HISTORY).on(
                Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(Tables.BOOK.ID).and(currentSalesUnitPriceCondition())
            ).where(condition)
        )
    }

    fun insert(request: BookCreateRequest): Long? {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        val now = LocalDateTime.now()
        return dsl.insertInto<BookRecord?>(Tables.BOOK).set<String?>(Tables.BOOK.TITLE, request.title)
            .set<String?>(Tables.BOOK.AUTHOR, request.author)
            .set<LocalDate?>(Tables.BOOK.RELEASE_DATE, request.releaseDate)
            .set<Long?>(Tables.BOOK.PUBLISHER_ID, request.publisherId).set<Long?>(Tables.BOOK.GENRE_ID, request.genreId)
            .set<String?>(Tables.BOOK.ISBN, request.isbn).set<LocalDateTime?>(Tables.BOOK.CREATE_AT, now)
            .set<LocalDateTime?>(Tables.BOOK.UPDATE_AT, now).set<Long?>(Tables.BOOK.VERSION, 1L)
            .returningResult<Long?>(Tables.BOOK.ID).fetchOne<Long?>(Tables.BOOK.ID)
    }

    fun insertSalesUnitPriceHistory(
        bookId: Long?,
        salesUnitPrice: Int?,
        effectiveFrom: LocalDate?,
        effectiveTo: LocalDate?,
        now: LocalDateTime?
    ) {
        dsl.insertInto<BookSalesUnitPriceHistoryRecord?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY)
            .set<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID, selectNextSalesUnitPriceHistoryId())
            .set<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID, bookId)
            .set<Int?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.SALES_UNIT_PRICE, salesUnitPrice)
            .set<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM, effectiveFrom)
            .set<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO, effectiveTo)
            .set<LocalDateTime?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.CREATE_AT, now)
            .set<LocalDateTime?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.UPDATE_AT, now)
            .set<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION, 1L).execute()
    }

    private fun selectNextSalesUnitPriceHistoryId(): Long? {
        return dsl.select<Long?>(
            DSL.coalesce<Long?>(DSL.max<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID), 0L).add(1L)
        ).from(Tables.BOOK_SALES_UNIT_PRICE_HISTORY).fetchOne<Long?>(0, Long::class.java)
    }

    fun selectFollowingSalesUnitPriceHistories(
        bookId: Long?,
        effectiveFrom: LocalDate?
    ): MutableList<BookSalesUnitPriceHistoryRow?> {
        return dsl.select<Long?, LocalDate?, Long?>(
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID,
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM,
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION
        ).from(Tables.BOOK_SALES_UNIT_PRICE_HISTORY).where(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(bookId))
            .and(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.ge(effectiveFrom))
            .orderBy<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM)
            .fetch<BookSalesUnitPriceHistoryRow?>(RecordMapper { row: Record3<Long?, LocalDate?, Long?>? ->
                BookSalesUnitPriceHistoryRow(
                    row!!.get<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID),
                    row.get<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM),
                    row.get<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
                )
            })
    }

    fun selectPreviousSalesUnitPriceHistory(bookId: Long?, effectiveFrom: LocalDate?): BookSalesUnitPriceHistoryRow? {
        return dsl.select<Long?, LocalDate?, Long?>(
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID,
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM,
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION
        ).from(Tables.BOOK_SALES_UNIT_PRICE_HISTORY).where(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.BOOK_ID.eq(bookId))
            .and(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.lt(effectiveFrom))
            .orderBy<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.desc()).limit(1)
            .fetchOne<BookSalesUnitPriceHistoryRow?>(RecordMapper { row: Record3<Long?, LocalDate?, Long?>? ->
                BookSalesUnitPriceHistoryRow(
                    row!!.get<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID),
                    row.get<LocalDate?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM),
                    row.get<Long?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION)
                )
            })
    }

    fun updateSalesUnitPriceHistoryEffectiveTo(history: BookSalesUnitPriceHistoryRow, effectiveTo: LocalDate?) {
        dsl.update<BookSalesUnitPriceHistoryRecord?>(Tables.BOOK_SALES_UNIT_PRICE_HISTORY)
            .set(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO, effectiveTo)
            .set(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.UPDATE_AT, LocalDateTime.now())
            .set(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.VERSION, history.version!! + 1)
            .where(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.ID.eq(history.id)).execute()
    }

    fun update(request: BookUpdateRequest, currentVersion: Long) {
        if (request == null) {
            throw NullPointerException("request is marked non-null but is null")
        }
        dsl.update<BookRecord?>(Tables.BOOK).set<String?>(Tables.BOOK.TITLE, request.title)
            .set<String?>(Tables.BOOK.AUTHOR, request.author)
            .set<LocalDate?>(Tables.BOOK.RELEASE_DATE, request.releaseDate)
            .set<Long?>(Tables.BOOK.PUBLISHER_ID, request.publisherId).set<Long?>(Tables.BOOK.GENRE_ID, request.genreId)
            .set<String?>(Tables.BOOK.ISBN, request.isbn)
            .set<LocalDateTime?>(Tables.BOOK.UPDATE_AT, LocalDateTime.now())
            .set<Long?>(Tables.BOOK.VERSION, currentVersion + 1).where(Tables.BOOK.ID.eq(request.id)).execute()
    }

    fun delete(id: Long) {
        if (id == null) {
            throw NullPointerException("id is marked non-null but is null")
        }
        dsl.deleteFrom<BookRecord?>(Tables.BOOK).where(Tables.BOOK.ID.eq(id)).execute()
    }

    private fun <T> executeWithLockException(supplier: Supplier<T?>): T? {
        try {
            return supplier.get()
        } catch (ex: DataAccessException) {
            throw PessimisticLockingFailureException("jOOQ write lock could not be acquired", ex)
        }
    }

    private fun currentSalesUnitPriceCondition(): Condition {
        val today = LocalDate.now()
        return Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_FROM.le(today).and(
            Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO.isNull()
                .or(Tables.BOOK_SALES_UNIT_PRICE_HISTORY.EFFECTIVE_TO.ge(today))
        )
    }
}
