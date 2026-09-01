# JPA 実装メモ

共通方針は `common.md`、スキーマ・採番は `schema-and-codegen.md` を参照してください。

## 構成

- 書籍機能は `BooksOperationServiceJPA`、`BookRepository`、`BookSalesUnitPriceHistoryRepository`、`PublisherRepository`、`BookGenreRepository` と対応 Entity を中心に構成します。
- 仕入機能は `PurchaseOperationServiceJPA`、`PurchaseOrderRepository`、`PurchaseOrderDetailRepository`、`BookStockRepository`、`BookStockMovementRepository`、validator、converter を中心に構成します。
- JPA profile 固有設定は `jpa/config` に置きます。
- `application.yaml` では Spring Data JPA repository を無効化し、`application-jpa.yaml` で有効化します。
- JPA auditing は `DemoApplication` ではなく、`jpa` profile の `JpaAuditingConfig` でのみ有効化します。

## Entity・query・変換

- `BookStockMovement.book` / `store` は暗黙の EAGER fetch を避けるため `FetchType.LAZY` を明示します。関連情報は join fetch、EntityGraph、DTO query などで取得範囲を明示します。
- 検索は Spring Data Repository メソッドまたは明示的な `@Query` を優先し、1書籍が複数在庫行になる取得では native query と projection を使用できます。
- 書籍取得・検索は `BookRepository.BookWithStockRowProjection` を使います。列変更時は projection、native query、`BookOperationConverterJPA` を揃えます。
- projection は在庫単位の行を返すため、`BookOperationConverterJPA.toResponseFrom(...)` / `toResponse(...)` で書籍単位に集約します。
- query は `publisher`、`book_genre`、現在有効な `book_sales_unit_price_history`、`book_stock`、`store` の結合を維持します。
- 一覧取得 query と count query の条件を揃え、先に書籍をページングしてから在庫・店舗を結合します。

## 採番・履歴・ロック

- Entity ID は `@GeneratedValue(strategy = GenerationType.SEQUENCE)` と各 `*_seq` の `@SequenceGenerator(allocationSize = 1)` で採番します。
- `BookSalesUnitPriceHistoryRepository.nextId` は手書き native INSERT 用に `book_sales_unit_price_history_seq` の次の値を取得します。
- 販売単価履歴追加は `BookSalesUnitPriceHistoryRepository` を使い、前後履歴の期間を調整します。
- 更新・削除は `findByIdWithWriteLock`、仕入在庫は `BookStockRepository.findByStoreIdAndBookIdWithWriteLock` を使います。
- 仕入登録はレスポンスを返す前に伝票、明細、在庫、在庫増減履歴の Repository を明示的に `flush()` し、制約違反や書き込みエラーをトランザクション内で検出します。既存の flush 順序を不用意に変更しません。

## validator・型変換

- 出版社・ジャンルの存在確認は `BookDataValidatorJPA` と各 Repository に集約します。
- ISBN 一意性と仕入明細 ISBN の解決は `BookRepository.findByIsbn` を使います。
- `PurchaseInvoiceTypeConverter`、`BookStockMovementTypeConverter`、`BookStockMovementSourceTypeConverter` で共有ドメイン型を DB の整数値に変換します。値変更時は DB CHECK 制約と他方式を揃えます。

## テスト

`BooksOperationServiceJPATest`、`PurchaseOperationServiceJPATest`、`BookDataValidatorJPATest`、`PurchaseDataValidatorJPATest` を確認します。
