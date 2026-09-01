# Doma 実装メモ

共通方針は `common.md`、スキーマ・採番は `schema-and-codegen.md` を参照してください。

## 生成コードと手書き SQL

- `src/main/java/com/example/demo/doma/generator` と `src/main/resources/META-INF/com/example/demo/doma/generator/dao` は Doma CodeGen の生成物なので直接編集しません。
- 手書き DAO は `BookCustomDao`、`BookStockCustomDao`、`BookSalesUnitPriceHistoryCustomDao` などに置き、SQL は `src/main/resources/META-INF/com/example/demo/doma/dao` 配下に置きます。
- DAO メソッド名と対応 SQL ファイルのパスを揃えます。
- 対象スキーマとテーブルは `generator-schema.sql`、`build.gradle` の `domaCodeGen` / `generatedTablePattern` と揃えます。
- ドメイン型の CodeGen 解決は `src/main/resources/codegen/entityPropertyClassNames.properties` で管理します。

## 検索・集約

- `BookWithPublisherName` は `publisherName`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` を持つ表示向け Entity です。
- 列変更時は SQL、`BookWithPublisherNameAggregateStrategy`、`BookOperationConverterDoma` を揃えます。
- `bookStockList` は `BookStockWithStoreName` と AggregateStrategy で集約します。在庫なしの書籍も返すため在庫・店舗の LEFT JOIN を維持します。
- ISBN 取得は `BookCustomDao.selectByIsbn` と `selectByIsbn.sql` を使います。
- 検索条件変更時は `selectByTitleOrAuthorStartingWithIgnoreCase.sql` と `countByTitleOrAuthorStartingWithIgnoreCase.sql` を対で更新します。
- 一覧は先に書籍を `limit` / `offset` でページングし、offset は `PageCalculator.calculateOffset` で計算します。

## 採番・履歴・ロック

- 生成 Entity ID は `@GeneratedValue(strategy = GenerationType.SEQUENCE)` と各 `*_seq` の `@SequenceGenerator(allocationSize = 1)` で採番します。
- Doma は INSERT 前に Entity ID が設定済みだとシーケンス採番しないため、登録 converter は主キー以外の項目を明示的に設定します。
- 販売単価履歴 ID は `BookSalesUnitPriceHistoryCustomDao.selectNextId` と `selectNextId.sql` で取得します。
- 販売単価履歴追加は `BookSalesUnitPriceHistoryCustomDao` で前後履歴を取得して期間を調整します。
- 本のロックは `selectByIdWithWriteLock.sql`、仕入在庫は `BookStockCustomDao.selectByStoreIdAndBookIdWithWriteLock` の `for update nowait` を使います。
- 仕入登録では伝票 INSERT 前に全明細の対象在庫を取得してロックする現在の順序を維持します。
- 在庫増減履歴は `BookStockMovementDao` で登録します。
- Doma の楽観ロック例外は `ObjectOptimisticLockingFailureException` に変換します。

## テスト

`BooksOperationServiceDomaTest`、`PurchaseOperationServiceDomaTest`、`PurchaseDataValidatorDomaTest` を確認します。本データバリデーションの変更は `BooksOperationServiceDomaTest` でも確認します。
