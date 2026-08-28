# 概要→詳細は[AGENTS.md](https://github.com/nagaokambeyond/samples/blob/main/spring_java_orm_sample/AGENTS.md)で

- framework→spring boot 4.1
- database→H2
- ORM
  - JPA
  - MyBatis
  - Doma3
  - jOOQ
- entityの自動生成あり
- api仕様
  - swagger-ui→http://localhost:8080/swagger-ui/index.html#/
  - scalar→http://localhost:8080/scalar

# gradleのタスク

```shell
./gradlew domaCodeGenAll
./gradlew runMyBatisGenerator
./gradlew generateJooq
./gradlew test
./gradlew clean nativeCompile
```

# ER図

```mermaid
erDiagram
    publisher ||--o{ book : "publishes"
    book_genre ||--o{ book : "categorizes"
    supplier ||--o{ purchase_invoice : "supplies"
    store ||--o{ purchase_invoice : "receives"
    purchase_invoice ||--o{ purchase_invoice : "return source"
    purchase_invoice ||--o{ purchase_invoice_detail : "has"
    book ||--o{ purchase_invoice_detail : "ordered"
    book ||--o{ book_sales_unit_price_history : "sales unit prices"
    store ||--o{ book_stock : "stocks"
    book ||--o{ book_stock : "stocked"
    store ||--o{ book_stock_movement : "stock movements"
    book ||--o{ book_stock_movement : "movement target"

    publisher {
        BIGINT id PK
        VARCHAR publisher_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book_genre {
        BIGINT id PK
        VARCHAR genre_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book {
        BIGINT id PK
        VARCHAR title
        VARCHAR author
        DATE release_date
        BIGINT publisher_id FK
        BIGINT genre_id FK
        VARCHAR isbn UK
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    supplier {
        BIGINT id PK
        VARCHAR supplier_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    store {
        BIGINT id PK
        VARCHAR store_name
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    purchase_invoice {
        BIGINT id PK
        INTEGER purchase_invoice_type
        BIGINT return_purchase_invoice_id FK
        DATE purchase_invoice_date
        BIGINT supplier_id FK
        BIGINT receiving_store_id FK
        BIGINT purchase_invoice_amount
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    purchase_invoice_detail {
        BIGINT id PK
        BIGINT purchase_invoice_id FK
        BIGINT purchase_invoice_detail_book_id FK
        INTEGER purchase_invoice_detail_unit_price
        INTEGER purchase_invoice_detail_quantity
        BIGINT purchase_invoice_detail_amount
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book_sales_unit_price_history {
        BIGINT id PK
        BIGINT book_id FK
        INTEGER sales_unit_price
        DATE effective_from
        DATE effective_to
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book_stock {
        BIGINT id PK
        BIGINT book_stock_store_id FK
        BIGINT book_stock_book_id FK
        INTEGER book_stock_quantity
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }

    book_stock_movement {
        BIGINT id PK
        BIGINT store_id FK
        BIGINT book_id FK
        INTEGER movement_type
        INTEGER quantity_delta
        INTEGER source_type
        BIGINT source_id
        BIGINT source_detail_id
        DATE movement_date
        TIMESTAMP create_at
        TIMESTAMP update_at
        BIGINT version
    }
```
# パフォーマンス比較

## graalvm

```bash
./gradlew clean nativeCompile
./build/native/nativeCompile/demo --spring.profiles.active=doma,native
```

```log
2026-08-19T07:27:13.697+09:00  INFO 6336 --- [demo] [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.295 seconds (process running for 0.32)
2026-08-19T07:28:24.412+09:00  INFO 6336 --- [demo] [nio-8080-exec-9] c.example.demo.api.log.ApiInterceptor    : ✅[API END] GET /api/books/1 -> 200 (13 ms)
2026-08-19T07:29:40.622+09:00  INFO 6336 --- [demo] [nio-8080-exec-6] c.example.demo.api.log.ApiInterceptor    : ✅[API END] POST /api/auth/login -> 200 (210 ms)
2026-08-19T07:31:29.111+09:00  INFO 6336 --- [demo] [nio-8080-exec-9] c.example.demo.api.log.ApiInterceptor    : ✅[API END] POST /api/books/create -> 200 (48 ms)
```

## jdk

```log
2026-08-19T22:15:19.361+09:00  INFO 7290 --- [demo] [  restartedMain] com.example.demo.DemoApplication         : Started DemoApplication in 3.859 seconds (process running for 4.187)
2026-08-19T22:16:13.668+09:00  INFO 7290 --- [demo] [nio-8080-exec-2] c.example.demo.api.log.ApiInterceptor    : ✅[API END] GET /api/books/1 -> 200 (97 ms)
2026-08-19T22:15:51.662+09:00  INFO 7290 --- [demo] [nio-8080-exec-1] c.example.demo.api.log.ApiInterceptor    : ✅[API END] POST /api/auth/login -> 200 (263 ms)
2026-08-19T22:18:11.665+09:00  INFO 7290 --- [demo] [nio-8080-exec-4] c.example.demo.api.log.ApiInterceptor    : ✅[API END] POST /api/books/create -> 200 (46 ms)
```
