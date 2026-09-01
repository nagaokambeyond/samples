# スキーマ・共有型・コード生成

## スキーマと主キー

現在の対象テーブルは `publisher`、`book_genre`、`book`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` です。

全テーブルの主キーは、テーブル単位の `publisher_seq`、`book_genre_seq`、`book_seq`、`supplier_seq`、`store_seq`、`purchase_invoice_seq`、`purchase_invoice_detail_seq`、`book_sales_unit_price_history_seq`、`book_stock_seq`、`book_stock_movement_seq` で採番します。IDENTITY や `max(id) + 1` は使いません。

シーケンスを追加・変更する場合は、次を揃えます。

- `generator-schema.sql`
- `data.sql` の `ALTER SEQUENCE ... RESTART WITH`
- JPA Entity / Repository
- `generatorConfig.xml` と MyBatis Mapper XML
- Doma 生成 Entity / 手書き SQL
- jOOQ DSL

`data.sql` は外部キー依存順に既存データを削除して初期データを投入し、最後に未使用の次の値へシーケンスを進めます。初期データの最大 ID と再開値をずらさないでください。

## スキーマ変更の確認範囲

カラムや制約を変更する場合は、該当する JPA Entity / Repository、MyBatis 生成物と手書き XML、Doma 生成物と手書き SQL、jOOQ 生成物と DSL、表示用 projection / Entity / row、AggregateStrategy、validator、converter、request / response DTO、`data.sql`、`generator-schema.sql`、各生成設定を確認します。

- `release_date`: API validation、検索条件、4方式の一覧・件数取得
- `isbn`: DB 一意制約、`@Isbn`、DTO、仕入明細の参照、validator、例外処理
- `publisher_id` / `genre_id`: 外部キー、初期データ、参照存在確認
- `salesUnitPrice`: 販売単価履歴、表示 row、検索 query / count、converter
- `bookStockList`: 在庫・店舗の結合、JPA projection、MyBatis collection、Doma AggregateStrategy、jOOQ row
- 仕入・在庫・販売単価履歴の外部キー: 伝票、明細、店舗、本、履歴の各関連

仕入・在庫・販売単価履歴の外部キーを変更する場合は、特に次の列をまとめて確認します。

- `purchase_invoice.return_purchase_invoice_id`
- `purchase_invoice.supplier_id`
- `purchase_invoice.receiving_store_id`
- `purchase_invoice_detail.purchase_invoice_id`
- `purchase_invoice_detail.purchase_invoice_detail_book_id`
- `book_stock.book_stock_store_id`
- `book_stock.book_stock_book_id`
- `book_stock_movement.store_id`
- `book_stock_movement.book_id`
- `book_sales_unit_price_history.book_id`

検索条件を変える場合は、4方式の一覧取得と件数取得が同じ条件になるよう確認します。

## 共有ドメイン型

共有ドメイン型は `src/main/java/com/example/demo/data/domain` に置き、DB CHECK 制約、初期データ、4方式の型変換を揃えます。

- `PurchaseInvoiceType`: `PURCHASE(1)`、`RETURN_PURCHASE(2)`
- `BookStockMovementType`: `INITIAL_STOCK(1)`、`PURCHASE(2)`、`SALE(3)`、`RETURN_PURCHASE(4)`、`SALES_RETURN(5)`、`STOCK_ADJUSTMENT(6)`、`STORE_TRANSFER_IN(7)`、`STORE_TRANSFER_OUT(8)`
- `BookStockMovementSourceType`: `PURCHASE_INVOICE(1)`、`SALES_ORDER(2)`、`STOCK_ADJUSTMENT(3)`、`STORE_TRANSFER(4)`

値を変更する場合は、`of(...)`、JPA converter、MyBatis TypeHandler と `columnOverride`、Doma `@Domain` と `entityPropertyClassNames.properties`、jOOQ の `getValue()` / `of(...)`、`data.sql`、`generator-schema.sql` の `check_purchase_invoice_type`、`check_book_stock_movement_type`、`check_book_stock_movement_source_type` を確認します。

MyBatis Generator の生成 Mapper は `BookMapper`、`PublisherMapper`、`BookGenreMapper`、`SupplierMapper`、`StoreMapper`、`PurchaseOrderMapper`、`PurchaseOrderDetailMapper`、`BookStockMapper`、`BookStockMovementMapper`、`BookSalesUnitPriceHistoryMapper` です。Doma CodeGen も対応する各テーブルの Entity、DAO、SQL を生成します。生成名を変える場合は Service、converter、手書き SQL、テストへの影響を確認します。

## 生成対象と生成物

- `generator-schema.sql` は MyBatis Generator、Doma CodeGen、jOOQ CodeGen で共有します。
- `build.gradle` の `generatedTablePattern` は3つの DB コード生成で共有します。
- MyBatis の生成設定は `generatorConfig.xml` です。
- Doma の型解決は `src/main/resources/codegen/entityPropertyClassNames.properties` です。
- jOOQ の設定は `src/main/resources/codegen/jooq-codegen-config.xml` です。
- OpenBD の生成元は `src/main/resources/openapi/openbd_api_spec.yaml` です。
- `src/main/java/com/example/demo/jooq/generated` と `src/main/java/com/example/demo/openbd/generated` を直接編集しません。

## 生成コマンド

生成物を更新する意図がある場合だけ実行します。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
./gradlew syncOpenBdGeneratedSources
```

MyBatis Generator と Doma CodeGen はファイルを上書きする可能性があります。`compileJava` は `generateJooq` と `syncOpenBdGeneratedSources` に依存します。生成前後と通常ビルド後に `git status --short` と差分を確認し、機械的な生成差分かを確認してください。
