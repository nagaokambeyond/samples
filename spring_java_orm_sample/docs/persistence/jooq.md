# jOOQ 実装メモ

共通方針は `common.md`、スキーマ・採番は `schema-and-codegen.md` を参照してください。

## 構成と生成コード

- 手書き実装は `src/main/java/com/example/demo/jooq`、生成コードは `jooq/generated` に置きます。生成コードは直接編集しません。
- 生成元は `generator-schema.sql`、設定テンプレートは `src/main/resources/codegen/jooq-codegen-config.xml` です。
- 生成対象は `build.gradle` の `generatedTablePattern` で MyBatis / Doma と共有します。
- `compileJava` は `generateJooq` と `syncOpenBdGeneratedSources` に依存するため、通常ビルド後も生成差分を確認します。
- jOOQ 実装は `jooq` profile で有効です。

## DSL・検索・変換

- 手書き SQL / DSL は `BookOperationDsl` / `PurchaseOperationDsl` に集約します。
- 参照存在チェックは `BookDsl`、`BookGenreDsl`、`PublisherDsl`、`StoreDsl`、`SupplierDsl` を使います。
- Service や validator に query を直接追加する前に、既存 DSL component の責務として追加できるか確認します。
- DSL component は `DSLContext` と `com.example.demo.jooq.generated.Tables` を使います。
- 書籍取得・検索は `BookWithStockRow` を返し、`BookOperationConverterJooq` で書籍単位に集約します。
- 販売単価履歴は `BookSalesUnitPriceHistoryRow`、仕入レスポンス変換は `PurchaseInvoiceRow` / `PurchaseInvoiceDetailRow` を使います。
- 一覧は先に書籍を `limit` / `offset` でページングし、検索 query と count query の条件を揃えます。

## 採番・履歴・ロック

- `BookOperationDsl` / `PurchaseOperationDsl` の `nextSequenceValue` で `*_seq` の ID を取得し、INSERT に明示設定します。`returning` や `max(id) + 1` に依存しません。
- 販売単価履歴追加は前後履歴の期間を調整します。
- 行ロックは `forUpdate().noWait()` を使い、失敗を `PessimisticLockingFailureException` に変換します。
- `PurchaseOperationDsl.insertBookStockMovement` で `PURCHASE` / `PURCHASE_INVOICE` の在庫増減履歴を登録します。

## 型変換

共有ドメイン型は DB 上では `Integer` として扱います。保存時は `PurchaseInvoiceType#getValue()`、`BookStockMovementType#getValue()`、`BookStockMovementSourceType#getValue()`、復元時は対応する `of(...)` を使います。

## テスト

`BooksOperationServiceJooqTest`、`PurchaseOperationServiceJooqTest`、`BookDataValidatorJooqTest`、`PurchaseDataValidatorJooqTest` を確認します。
