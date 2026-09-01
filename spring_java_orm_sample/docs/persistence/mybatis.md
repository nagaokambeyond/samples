# MyBatis 実装メモ

共通方針は `common.md`、スキーマ・採番は `schema-and-codegen.md` を参照してください。

## 生成コードと手書き SQL

- `src/main/java/com/example/demo/mybatis/generator` と `src/main/resources/com/example/demo/mybatis/generator` は MyBatis Generator の生成物なので直接編集しません。
- 手書き SQL は `BookCustomMapper`、`BookStockCustomMapper` と対応する `src/main/resources/com/example/demo/mybatis/mapper` 配下の XML に追加します。
- `application.yaml` の `mybatis.mapper-locations` は XML 読み込みに必要です。
- 生成対象や生成名は `generator-schema.sql`、`generatorConfig.xml`、`build.gradle` の `generatedTablePattern` と揃えます。

`purchase_invoice` / `purchase_invoice_detail` は `PurchaseOrderEntity` / `PurchaseOrderDetailEntity`、`PurchaseOrderMapper` / `PurchaseOrderDetailMapper` として生成します。`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` も対応する `BookStock*`、`BookStockMovement*`、`BookSalesUnitPriceHistory*` として生成します。

## 検索・変換

- `BookWithPublisherName` は `publisherName`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` を持つ表示向け Entity です。
- 列変更時は resultMap、nested collection、SQL、`BookOperationConverterMybatis` を揃えます。
- `bookStockList` は `BookStockWithStoreName` と `BookCustomMapper.xml` の `<collection>` で組み立て、`notNullColumn="bs_id"` と `bs_*` alias を維持します。
- 検索条件変更時は `selectByTitleOrAuthorStartingWithIgnoreCase` と `countByTitleOrAuthorStartingWithIgnoreCase` を対で更新します。
- 一覧は先に書籍を `limit` / `offset` でページングし、offset は `PageCalculator.calculateOffset` で計算します。

## 採番・履歴・ロック

- `generatorConfig.xml` の `generatedKey` は `SELECT NEXT VALUE FOR *_seq`、生成 XML は `selectKey order="BEFORE"` で INSERT 前に ID を設定します。
- 手書きの `BookCustomMapper.insertWithGeneratedKey` も同じ方式を使います。
- converter は主キーを設定せず、Mapper の採番処理へ委ねます。
- 販売単価履歴 ID は `BookCustomMapper.selectNextSalesUnitPriceHistoryId` で取得し、`max(id) + 1` に戻しません。
- 販売単価履歴追加は `BookSalesUnitPriceHistoryMapper` と `BookCustomMapper` を使って前後履歴を調整します。
- 本の書き込みロックは `selectByPrimaryKeyWithWriteLock`、仕入在庫は `BookStockCustomMapper.selectByStoreIdAndBookIdWithWriteLock` の `for update nowait` を使います。
- 在庫増減履歴は `BookStockMovementMapper` で登録します。

## 型変換

MyBatis TypeHandler は `src/main/resources/mybatis-config.xml` に登録します。`PurchaseInvoiceTypeHandler`、`BookStockMovementTypeHandler`、`BookStockMovementSourceTypeHandler` の変更時は `generatorConfig.xml` の `columnOverride` も確認します。

## テスト

`BooksOperationServiceMybatisTest`、`PurchaseOperationServiceMybatisTest`、`BookDataValidatorMybatisTest`、`PurchaseDataValidatorMybatisTest` を確認します。
