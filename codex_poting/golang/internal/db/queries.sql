-- name: GetBookBase :one
SELECT
    b.id,
    b.title,
    b.author,
    b.release_date,
    b.publisher_id,
    p.publisher_name,
    b.genre_id,
    g.genre_name,
    b.isbn,
    h.sales_unit_price,
    b.update_at,
    b.version
FROM book b
JOIN publisher p ON p.id = b.publisher_id
JOIN book_genre g ON g.id = b.genre_id
JOIN book_sales_unit_price_history h
    ON h.book_id = b.id
   AND h.effective_from <= sqlc.arg(today)
   AND (h.effective_to IS NULL OR sqlc.arg(today) <= h.effective_to)
WHERE b.id = sqlc.arg(id);

-- name: ListBookStocks :many
SELECT
    bs.id,
    bs.book_stock_store_id,
    s.store_name,
    bs.book_stock_quantity
FROM book_stock bs
JOIN store s ON s.id = bs.book_stock_store_id
WHERE bs.book_stock_book_id = sqlc.arg(book_id)
ORDER BY bs.book_stock_store_id, bs.id;

-- name: SearchBookBases :many
SELECT
    b.id,
    b.title,
    b.author,
    b.release_date,
    b.publisher_id,
    p.publisher_name,
    b.genre_id,
    g.genre_name,
    b.isbn,
    h.sales_unit_price,
    b.update_at,
    b.version
FROM book b
JOIN publisher p ON p.id = b.publisher_id
JOIN book_genre g ON g.id = b.genre_id
JOIN book_sales_unit_price_history h
    ON h.book_id = b.id
   AND h.effective_from <= sqlc.arg(today)
   AND (h.effective_to IS NULL OR sqlc.arg(today) <= h.effective_to)
WHERE
    (sqlc.arg(keyword) = '' OR lower(b.title) LIKE lower(sqlc.arg(keyword) || '%') OR lower(COALESCE(b.author, '')) LIKE lower(sqlc.arg(keyword) || '%'))
    AND (sqlc.arg(release_date_from) = '' OR (b.release_date >= sqlc.arg(release_date_from) AND b.release_date <= sqlc.arg(release_date_to)))
ORDER BY b.id
LIMIT sqlc.arg(limit_rows) OFFSET sqlc.arg(offset_rows);

-- name: CountBookSearch :one
SELECT count(*)
FROM book b
JOIN book_sales_unit_price_history h
    ON h.book_id = b.id
   AND h.effective_from <= sqlc.arg(today)
   AND (h.effective_to IS NULL OR sqlc.arg(today) <= h.effective_to)
WHERE
    (sqlc.arg(keyword) = '' OR lower(b.title) LIKE lower(sqlc.arg(keyword) || '%') OR lower(COALESCE(b.author, '')) LIKE lower(sqlc.arg(keyword) || '%'))
    AND (sqlc.arg(release_date_from) = '' OR (b.release_date >= sqlc.arg(release_date_from) AND b.release_date <= sqlc.arg(release_date_to)));

-- name: ExistsPublisher :one
SELECT EXISTS(SELECT 1 FROM publisher WHERE id = sqlc.arg(id));

-- name: ExistsGenre :one
SELECT EXISTS(SELECT 1 FROM book_genre WHERE id = sqlc.arg(id));

-- name: ExistsSupplier :one
SELECT EXISTS(SELECT 1 FROM supplier WHERE id = sqlc.arg(id));

-- name: ExistsStore :one
SELECT EXISTS(SELECT 1 FROM store WHERE id = sqlc.arg(id));

-- name: ExistsBookID :one
SELECT EXISTS(SELECT 1 FROM book WHERE id = sqlc.arg(id));

-- name: FindBookByISBN :one
SELECT id FROM book WHERE isbn = sqlc.arg(isbn);

-- name: CreateBook :one
INSERT INTO book (title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version)
VALUES (sqlc.arg(title), sqlc.narg(author), sqlc.arg(release_date), sqlc.arg(publisher_id), sqlc.arg(genre_id), sqlc.arg(isbn), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;

-- name: UpdateBook :execrows
UPDATE book
SET title = sqlc.arg(title),
    author = sqlc.narg(author),
    release_date = sqlc.arg(release_date),
    publisher_id = sqlc.arg(publisher_id),
    genre_id = sqlc.arg(genre_id),
    isbn = sqlc.arg(isbn),
    update_at = sqlc.arg(now),
    version = version + 1
WHERE id = sqlc.arg(id) AND version = sqlc.arg(version);

-- name: DeleteBook :execrows
DELETE FROM book WHERE id = sqlc.arg(id);

-- name: CreateSalesUnitPriceHistory :one
INSERT INTO book_sales_unit_price_history (book_id, sales_unit_price, effective_from, effective_to, create_at, update_at, version)
VALUES (sqlc.arg(book_id), sqlc.arg(sales_unit_price), sqlc.arg(effective_from), sqlc.narg(effective_to), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;

-- name: GetNextSalesUnitPriceHistory :one
SELECT id, effective_from
FROM book_sales_unit_price_history
WHERE book_id = sqlc.arg(book_id) AND effective_from >= sqlc.arg(effective_from)
ORDER BY effective_from ASC
LIMIT 1;

-- name: GetPrevSalesUnitPriceHistory :one
SELECT id, effective_from
FROM book_sales_unit_price_history
WHERE book_id = sqlc.arg(book_id) AND effective_from < sqlc.arg(effective_from)
ORDER BY effective_from DESC
LIMIT 1;

-- name: UpdateSalesUnitPriceHistoryEffectiveTo :execrows
UPDATE book_sales_unit_price_history
SET effective_to = sqlc.arg(effective_to), update_at = sqlc.arg(now), version = version + 1
WHERE id = sqlc.arg(id);

-- name: CreatePurchaseInvoice :one
INSERT INTO purchase_invoice (purchase_invoice_type, return_purchase_invoice_id, purchase_invoice_date, supplier_id, receiving_store_id, purchase_invoice_amount, create_at, update_at, version)
VALUES (1, NULL, sqlc.arg(purchase_invoice_date), sqlc.arg(supplier_id), sqlc.arg(receiving_store_id), sqlc.arg(purchase_invoice_amount), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;

-- name: CreatePurchaseInvoiceDetail :one
INSERT INTO purchase_invoice_detail (purchase_invoice_id, purchase_invoice_detail_book_id, purchase_invoice_detail_unit_price, purchase_invoice_detail_quantity, purchase_invoice_detail_amount, create_at, update_at, version)
VALUES (sqlc.arg(purchase_invoice_id), sqlc.arg(book_id), sqlc.arg(unit_price), sqlc.arg(quantity), sqlc.arg(amount), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;

-- name: GetPurchaseInvoice :one
SELECT id, purchase_invoice_type, return_purchase_invoice_id, purchase_invoice_date, supplier_id, receiving_store_id, purchase_invoice_amount, update_at, version
FROM purchase_invoice
WHERE id = sqlc.arg(id);

-- name: ListPurchaseInvoiceDetails :many
SELECT id, purchase_invoice_id, purchase_invoice_detail_book_id, purchase_invoice_detail_unit_price, purchase_invoice_detail_quantity, purchase_invoice_detail_amount, update_at, version
FROM purchase_invoice_detail
WHERE purchase_invoice_id = sqlc.arg(purchase_invoice_id)
ORDER BY id;

-- name: CreateBookStockMovement :one
INSERT INTO book_stock_movement (store_id, book_id, movement_type, quantity_delta, source_type, source_id, source_detail_id, movement_date, create_at, update_at, version)
VALUES (sqlc.arg(store_id), sqlc.arg(book_id), 2, sqlc.arg(quantity_delta), 1, sqlc.arg(source_id), sqlc.arg(source_detail_id), sqlc.arg(movement_date), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;

-- name: GetBookStock :one
SELECT id, book_stock_quantity, version
FROM book_stock
WHERE book_stock_store_id = sqlc.arg(store_id) AND book_stock_book_id = sqlc.arg(book_id);

-- name: AddBookStockQuantity :execrows
UPDATE book_stock
SET book_stock_quantity = book_stock_quantity + sqlc.arg(quantity_delta),
    update_at = sqlc.arg(now),
    version = version + 1
WHERE id = sqlc.arg(id);

-- name: CreateBookStock :one
INSERT INTO book_stock (book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version)
VALUES (sqlc.arg(store_id), sqlc.arg(book_id), sqlc.arg(quantity), sqlc.arg(now), sqlc.arg(now), 1)
RETURNING id;
