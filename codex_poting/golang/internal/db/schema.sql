PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS publisher (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    publisher_name TEXT NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS book_genre (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    genre_name TEXT NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS book (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT,
    release_date TEXT NOT NULL,
    publisher_id INTEGER NOT NULL,
    genre_id INTEGER NOT NULL,
    isbn TEXT NOT NULL UNIQUE,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    FOREIGN KEY (publisher_id) REFERENCES publisher(id),
    FOREIGN KEY (genre_id) REFERENCES book_genre(id)
);

CREATE INDEX IF NOT EXISTS idx_book_01 ON book(release_date);
CREATE INDEX IF NOT EXISTS idx_book_02 ON book(publisher_id);
CREATE INDEX IF NOT EXISTS idx_book_03 ON book(genre_id);
CREATE INDEX IF NOT EXISTS idx_book_04 ON book(title);
CREATE INDEX IF NOT EXISTS idx_book_05 ON book(author);

CREATE TABLE IF NOT EXISTS supplier (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    supplier_name TEXT NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS store (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    store_name TEXT NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS purchase_invoice (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    purchase_invoice_type INTEGER NOT NULL CHECK (purchase_invoice_type IN (1, 2)),
    return_purchase_invoice_id INTEGER,
    purchase_invoice_date TEXT NOT NULL,
    supplier_id INTEGER NOT NULL,
    receiving_store_id INTEGER NOT NULL,
    purchase_invoice_amount INTEGER NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    FOREIGN KEY (return_purchase_invoice_id) REFERENCES purchase_invoice(id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    FOREIGN KEY (receiving_store_id) REFERENCES store(id)
);

CREATE INDEX IF NOT EXISTS idx_purchase_invoice_01 ON purchase_invoice(purchase_invoice_type);
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_02 ON purchase_invoice(return_purchase_invoice_id);
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_03 ON purchase_invoice(purchase_invoice_date);
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_04 ON purchase_invoice(supplier_id);
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_05 ON purchase_invoice(receiving_store_id);

CREATE TABLE IF NOT EXISTS purchase_invoice_detail (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    purchase_invoice_id INTEGER NOT NULL,
    purchase_invoice_detail_book_id INTEGER NOT NULL,
    purchase_invoice_detail_unit_price INTEGER NOT NULL,
    purchase_invoice_detail_quantity INTEGER NOT NULL,
    purchase_invoice_detail_amount INTEGER NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    FOREIGN KEY (purchase_invoice_id) REFERENCES purchase_invoice(id),
    FOREIGN KEY (purchase_invoice_detail_book_id) REFERENCES book(id)
);

CREATE INDEX IF NOT EXISTS idx_purchase_invoice_detail_01 ON purchase_invoice_detail(purchase_invoice_id);
CREATE INDEX IF NOT EXISTS idx_purchase_invoice_detail_02 ON purchase_invoice_detail(purchase_invoice_detail_book_id);

CREATE TABLE IF NOT EXISTS book_sales_unit_price_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL,
    sales_unit_price INTEGER NOT NULL CHECK (sales_unit_price BETWEEN 1 AND 10000),
    effective_from TEXT NOT NULL,
    effective_to TEXT,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    UNIQUE (book_id, effective_from),
    CHECK (effective_to IS NULL OR effective_from <= effective_to),
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_book_sales_unit_price_history_01 ON book_sales_unit_price_history(book_id);
CREATE INDEX IF NOT EXISTS idx_book_sales_unit_price_history_02 ON book_sales_unit_price_history(effective_from);
CREATE INDEX IF NOT EXISTS idx_book_sales_unit_price_history_03 ON book_sales_unit_price_history(effective_to);

CREATE TABLE IF NOT EXISTS book_stock (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_stock_store_id INTEGER NOT NULL,
    book_stock_book_id INTEGER NOT NULL,
    book_stock_quantity INTEGER NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    UNIQUE (book_stock_store_id, book_stock_book_id),
    FOREIGN KEY (book_stock_store_id) REFERENCES store(id),
    FOREIGN KEY (book_stock_book_id) REFERENCES book(id)
);

CREATE INDEX IF NOT EXISTS idx_book_stock_01 ON book_stock(book_stock_store_id);
CREATE INDEX IF NOT EXISTS idx_book_stock_02 ON book_stock(book_stock_book_id);
CREATE INDEX IF NOT EXISTS idx_book_stock_03 ON book_stock(book_stock_store_id, book_stock_book_id);

CREATE TABLE IF NOT EXISTS book_stock_movement (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    store_id INTEGER NOT NULL,
    book_id INTEGER NOT NULL,
    movement_type INTEGER NOT NULL CHECK (movement_type IN (1, 2, 3, 4, 5, 6, 7, 8)),
    quantity_delta INTEGER NOT NULL,
    source_type INTEGER CHECK (source_type IS NULL OR source_type IN (1, 2, 3, 4)),
    source_id INTEGER,
    source_detail_id INTEGER,
    movement_date TEXT NOT NULL,
    create_at TEXT NOT NULL,
    update_at TEXT NOT NULL,
    version INTEGER NOT NULL,
    FOREIGN KEY (store_id) REFERENCES store(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_book_stock_movement_01 ON book_stock_movement(store_id, book_id);
CREATE INDEX IF NOT EXISTS idx_book_stock_movement_02 ON book_stock_movement(movement_date);
CREATE INDEX IF NOT EXISTS idx_book_stock_movement_03 ON book_stock_movement(movement_type);
