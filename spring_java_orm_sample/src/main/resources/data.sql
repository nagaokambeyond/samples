MERGE INTO publisher (id, publisher_name, create_at, update_at, version) VALUES (1, '◯◯書房', '2026-01-01', '2026-01-01', 0);
MERGE INTO publisher (id, publisher_name, create_at, update_at, version) VALUES (2, '△△出版', '2026-01-01', '2026-01-01', 0);
MERGE INTO publisher (id, publisher_name, create_at, update_at, version) VALUES (3, '□□パブリッシング', '2026-01-01', '2026-01-01', 0);

MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (1, '雑誌', '2026-01-01', '2026-01-01', 0);
MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (2, '小説', '2026-01-01', '2026-01-01', 0);
MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (3, '健康', '2026-01-01', '2026-01-01', 0);
MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (4, '学習', '2026-01-01', '2026-01-01', 0);
MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (5, '工学', '2026-01-01', '2026-01-01', 0);
MERGE INTO book_genre (id, genre_name, create_at, update_at, version) KEY(id) VALUES (6, '音楽', '2026-01-01', '2026-01-01', 0);

MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (1, 'Spring入門', 'Taro', '2020-01-01', 1, 5, '0000000000001', '2026-01-01', '2026-01-01', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (2, 'はじめてのH2', 'Hanako', '2020-02-01', 2, 5, '0000000000002', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (3, 'はじめてのH3', 'Hanako', '2020-02-01', 2, 5, '0000000000003', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (4, 'はじめてのH4', 'Hanako', '2020-02-01', 2, 5, '0000000000004', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (5, 'はじめてのH5', 'Hanako', '2020-02-01', 2, 5, '0000000000005', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (6, 'はじめてのH6', 'Hanako', '2020-02-01', 2, 5, '0000000000006', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (7, 'はじめてのH7', 'Hanako', '2020-02-01', 2, 5, '0000000000007', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (8, 'はじめてのH8', 'Hanako', '2020-02-01', 2, 5, '0000000000008', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (9, 'はじめてのH9', 'Hanako', '2020-02-01', 2, 5, '0000000000009', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (10, 'はじめてのH10', 'Hanako', '2020-02-01', 2, 5, '0000000000010', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (11, 'はじめてのH11', 'Hanako', '2020-02-01', 2, 5, '0000000000011', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (12, 'はじめてのH12', 'Hanako', '2020-02-01', 2, 5, '0000000000012', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (13, 'はじめてのH13', 'Hanako', '2020-02-01', 2, 5, '0000000000013', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (14, 'はじめてのH14', 'Hanako', '2020-02-01', 2, 5, '0000000000014', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (15, 'はじめてのH15', 'Hanako', '2020-02-01', 2, 5, '0000000000015', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (16, 'はじめてのH16', 'Hanako', '2020-02-01', 2, 5, '0000000000016', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (17, 'はじめてのH17', 'Hanako', '2020-02-01', 2, 5, '0000000000017', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (18, 'はじめてのH18', 'Hanako', '2020-02-01', 2, 5, '0000000000018', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (19, 'はじめてのH19', 'Hanako', '2020-02-01', 2, 5, '0000000000019', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (20, 'はじめてのH20', 'Hanako', '2020-02-01', 2, 5, '0000000000020', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book (id, title, author, release_date, publisher_id, genre_id, isbn, create_at, update_at, version) VALUES (21, 'はじめてのH21', 'Hanako', '2020-02-01', 2, 5, '0000000000021', '2026-01-01', '2026-01-01 23:59:59', 0);

MERGE INTO supplier (id, supplier_name, create_at, update_at, version) VALUES (1, 'A取次', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO supplier (id, supplier_name, create_at, update_at, version) VALUES (2, 'B取次', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO supplier (id, supplier_name, create_at, update_at, version) VALUES (3, 'C取次', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO supplier (id, supplier_name, create_at, update_at, version) VALUES (4, 'D取次', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO supplier (id, supplier_name, create_at, update_at, version) VALUES (5, 'E取次', '2026-01-01', '2026-01-01 23:59:59', 0);

MERGE INTO store (id, store_name, create_at, update_at, version) VALUES (1, 'あ駅前店', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO store (id, store_name, create_at, update_at, version) VALUES (2, 'い駅前店', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO store (id, store_name, create_at, update_at, version) VALUES (3, 'う駅前店', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO store (id, store_name, create_at, update_at, version) VALUES (4, 'え駅前店', '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO store (id, store_name, create_at, update_at, version) VALUES (5, 'あ駅前店', '2026-01-01', '2026-01-01 23:59:59', 0);

MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (1, 1, 1, 10, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (2, 2, 1, 20, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (3, 3, 1, 30, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (4, 1, 2, 11, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (5, 2, 2, 21, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (6, 3, 2, 31, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (7, 4, 3, 12, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (8, 5, 4, 22, '2026-01-01', '2026-01-01 23:59:59', 0);
MERGE INTO book_stock (id, book_stock_store_id, book_stock_book_id, book_stock_quantity, create_at, update_at, version) VALUES (9, 5, 5, 32, '2026-01-01', '2026-01-01 23:59:59', 0);

ALTER TABLE publisher ALTER COLUMN id RESTART WITH 4;
ALTER TABLE book_genre ALTER COLUMN id RESTART WITH 7;
ALTER TABLE book ALTER COLUMN id RESTART WITH 22;
ALTER TABLE supplier ALTER COLUMN id RESTART WITH 6;
ALTER TABLE store ALTER COLUMN id RESTART WITH 6;
ALTER TABLE book_stock ALTER COLUMN id RESTART WITH 10;
