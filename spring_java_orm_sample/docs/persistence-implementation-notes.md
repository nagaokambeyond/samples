# 永続化実装メモ

## 全体方針

- 現在のデフォルト profile は `application.yaml` の `spring.profiles.default: doma` です。通常起動では `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` が使われます。
- Doma 実装の `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` には `@Primary` が付いています。複数 profile を同時に有効化する場合は、Service Bean の優先順位を確認してください。
- `BooksOperationService` は JPA / MyBatis / Doma / jOOQ 共通の Service インターフェースです。
- `PurchaseOperationService` は JPA / MyBatis / Doma / jOOQ 共通の仕入登録 Service インターフェースです。
- `BookDataValidatorJPA` / `BookDataValidatorMybatis` / `BookDataValidatorDoma` / `BookDataValidatorJooq` は永続化方式ごとの本データバリデーションを扱います。
- `PurchaseDataValidatorJPA` / `PurchaseDataValidatorMybatis` / `PurchaseDataValidatorDoma` / `PurchaseDataValidatorJooq` は永続化方式ごとの仕入データバリデーションを扱います。
- `BookOperationConverterJPA` / `BookOperationConverterMybatis` / `BookOperationConverterDoma` / `BookOperationConverterJooq` は projection / 表示向け Entity / row から response DTO への変換を扱います。JPA と jOOQ の行データは converter で書籍単位に集約し、`salesUnitPrice` と `bookStockList` を組み立てます。
- `PurchaseOperationConverterJPA` / `PurchaseOperationConverterMybatis` / `PurchaseOperationConverterDoma` / `PurchaseOperationConverterJooq` は仕入登録用の Entity / row、在庫増減履歴、金額計算、response DTO への変換を扱います。
- `book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id` を参照します。`book_sales_unit_price_history.book_id` は `book.id` を参照します。
- `book.isbn` は 13 桁の一意な ISBN として扱います。登録・更新時は各 `BookDataValidator*` で一意性を確認し、仕入登録時は明細 ISBN から本 ID を解決します。
- `book_sales_unit_price_history` は本の販売単価履歴を扱います。現在単価は `effective_from <= current_date` かつ `effective_to IS NULL OR current_date <= effective_to` の履歴から取得します。
- 現在のスキーマには `publisher`、`book_genre`、`book`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` があります。
- `purchase_invoice.purchase_invoice_type` は `PurchaseInvoiceType` で扱います。JPA / MyBatis / Doma / jOOQ の型変換設定を揃えてください。
- `book_stock_movement.movement_type` は `BookStockMovementType`、`book_stock_movement.source_type` は `BookStockMovementSourceType` で扱います。JPA / MyBatis / Doma / jOOQ の型変換または値変換の設定を揃えてください。
- profile や Spring Data JPA repository の有効化設定を変更する場合は、`application.yaml` と `application-jpa.yaml` の両方を確認してください。
- Service 内のメソッドで排他をかけてデータを取得する箇所があれば、メソッドに `@RetryableOnLockFailure` を付けてリトライします。
- ページング検索の offset と totalPages は `PageCalculator` を使って計算します。各 Service 実装で計算式を重複させないでください。
- JPA / MyBatis / Doma / jOOQ のうち1つの実装を変更する場合でも、他の実装で同じ仕様が必要か確認してください。

## jOOQ

- `src/main/java/com/example/demo/jooq` 配下は jOOQ 手書き実装です。
- jOOQ 生成コードは `src/main/java/com/example/demo/jooq/generated` 配下に生成します。
- 生成元スキーマは MyBatis Generator / Doma CodeGen と同じ `src/main/resources/generator-schema.sql` です。
- jOOQ CodeGen のテンプレートは `src/main/resources/codegen/jooq-codegen-config.xml` です。`build.gradle` の `generateJooq` タスクでテンプレート変数を置換して生成します。
- `compileJava` は `generateJooq` に依存しています。通常のビルドでも jOOQ 生成コードが更新される可能性があるため、生成差分を確認してください。
- jOOQ の生成対象テーブルは `build.gradle` の `generatedTablePattern` で管理しています。テーブル追加・削除時は MyBatis / Doma と合わせて更新してください。
- jOOQ の手書き SQL / DSL 組み立ては `BookOperationDsl` / `PurchaseOperationDsl` に集約します。Service 実装は DSL component、validator、converter を組み合わせ、API には既存の request / response DTO を返します。
- jOOQ の DSL component は `DSLContext` と `com.example.demo.jooq.generated.Tables` を使います。Service に新しい jOOQ クエリを直接増やす前に、既存 DSL component の責務として追加できるか確認してください。
- 本検索・取得では `BookWithStockRow` を使い、`BookOperationConverterJooq` で書籍単位に集約します。
- 本検索・取得では `book_sales_unit_price_history` を現在日付で結合し、`BookResponse.salesUnitPrice` を返します。件数取得でも同じ現在単価条件を維持してください。
- 販売単価履歴の追加では `BookSalesUnitPriceHistoryRow` を使い、前履歴の `effective_to` を新履歴の前日に更新し、後続履歴がある場合は新履歴の `effective_to` を後続履歴の前日にします。
- 仕入登録では `PurchaseInvoiceRow` / `PurchaseInvoiceDetailRow` を response 変換用 row として使います。
- `purchase_invoice_type` は jOOQ では DB 上の `Integer` として扱い、保存時は `PurchaseInvoiceType#getValue()`、レスポンス変換時は `PurchaseInvoiceType.of(...)` を使います。
- 一覧検索で在庫・店舗を結合する場合は、先に書籍を `limit` / `offset` でページングしてから `book_stock` / `store` を結合してください。在庫行の重複でページング件数が崩れないようにします。
- jOOQ の行ロック取得は `BookOperationDsl` / `PurchaseOperationDsl` で `forUpdate().noWait()` を使い、ロック失敗は `PessimisticLockingFailureException` に変換して既存の `RetryableOnLockFailure` / `GlobalExceptionHandler` に乗せます。
- jOOQ 生成コードには `book_stock_movement` と `book_sales_unit_price_history` も含まれます。jOOQ 側では在庫増減種別を DB 上の `Integer` として扱い、保存時は `BookStockMovementType#getValue()` / `BookStockMovementSourceType#getValue()` を使います。
- 仕入登録では `PurchaseOperationDsl.insertBookStockMovement` で `book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` の履歴を登録します。
- jOOQ 実装は `jooq` profile で有効になります。既定の永続化方式を変更する場合は、`application.yaml` の `spring.profiles.default`、各実装の `@Profile`、Doma の `@Primary` を合わせて確認してください。

## Doma

- `src/main/java/com/example/demo/doma/generator` 配下は Doma CodeGen の生成コードです。手作業での編集は避けてください。
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao` 配下の SQL も生成物として扱ってください。
- 手書き SQL は `BookCustomDao` / `BookStockCustomDao` / `BookSalesUnitPriceHistoryCustomDao` と `src/main/resources/META-INF/com/example/demo/doma/dao` 配下に追加してください。
- Doma の DAO メソッドを追加する場合は、対応する SQL ファイルのパスとメソッド名を揃えてください。
- `BookWithPublisherName` は Doma 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は SQL、`BookWithPublisherNameAggregateStrategy`、`BookOperationConverterDoma` も更新してください。
- `BookWithPublisherName` は `publisherName`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` を含みます。取得・検索 SQL では `publisher`、`book_genre`、`book_sales_unit_price_history`、`book_stock`、`store` の結合を維持してください。
- 本検索・取得では `book_sales_unit_price_history` を現在日付で結合し、`BookResponse.salesUnitPrice` を返します。検索 count SQL でも同じ現在単価条件を維持してください。
- 販売単価履歴の追加では `BookSalesUnitPriceHistoryCustomDao` で前後の履歴を取得し、前履歴の `effective_to` を新履歴の前日に更新し、後続履歴がある場合は新履歴の `effective_to` を後続履歴の前日にします。
- ISBN による本取得は `BookCustomDao.selectByIsbn` と `selectByIsbn.sql` を使います。仕入登録の明細 ISBN 参照チェックや ISBN 一意性チェックを変更する場合はこの SQL も確認してください。
- `bookStockList` は `BookStockWithStoreName` と `BookWithPublisherNameAggregateStrategy` で集約します。`book_stock` が存在しない書籍も返せるよう、在庫・店舗は LEFT JOIN を維持してください。
- 一覧検索で在庫・店舗を結合する場合は、先に書籍を `limit` / `offset` でページングしてから `book_stock` / `store` を結合してください。在庫行の重複でページング件数が崩れないようにします。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleOrAuthorStartingWithIgnoreCase.sql` と `countByTitleOrAuthorStartingWithIgnoreCase.sql` の両方を更新してください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル/著者条件を付けない方針を維持してください。
- 検索では `limit` / `offset` を使います。offset は `PageCalculator.calculateOffset(page, size)` で算出してください。
- `selectByIdWithWriteLock.sql` は `for update nowait` を使います。ロック失敗時のリトライ方針と合わせて変更してください。
- `BookStockCustomDao/selectByStoreIdAndBookIdWithWriteLock.sql` は仕入登録時の在庫加算で使います。ロック方針を変更する場合は JPA / MyBatis / jOOQ 側も揃えてください。
- `BookDao` / `PublisherDao` / `BookGenreDao` / `SupplierDao` / `StoreDao` / `PurchaseInvoiceDao` / `PurchaseInvoiceDetailDao` / `BookStockDao` / `BookStockMovementDao` / `BookSalesUnitPriceHistoryDao` は Doma CodeGen の生成 DAO です。生成元スキーマとの整合性を維持してください。
- Doma CodeGen の対象スキーマを変える場合は、`generator-schema.sql` と `build.gradle` の `domaCodeGen` 設定の整合性を確認してください。
- Doma CodeGen の対象テーブルは `build.gradle` の `generatedTablePattern` 経由で管理しています。テーブル追加・削除時は MyBatis / jOOQ と合わせて更新してください。
- Doma CodeGen の型解決は `src/main/resources/codegen/entityPropertyClassNames.properties` も参照します。`PurchaseInvoiceType`、`BookStockMovementType`、`BookStockMovementSourceType` などのドメイン型を追加・変更する場合はこのファイルも確認してください。
- Doma 仕入登録では `BookStockMovementDao` で `book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` の履歴を登録します。
- Doma 側の更新・削除では、Doma の楽観ロック例外を `ObjectOptimisticLockingFailureException` に変換する既存方針を維持してください。

## MyBatis

- `src/main/java/com/example/demo/mybatis/generator` 配下は MyBatis Generator の生成コードです。手作業での編集は避けてください。
- `src/main/resources/com/example/demo/mybatis/generator` 配下の XML も生成物として扱ってください。
- 手書き SQL は `BookCustomMapper` / `BookStockCustomMapper` と対応する XML に追加してください。販売単価履歴の ID 採番付き登録は `BookCustomMapper` に置かれています。
- `BookWithPublisherName` は MyBatis 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は resultMap、nested collection、`BookOperationConverterMybatis` も更新してください。
- `BookWithPublisherName` は `publisherName`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` を含みます。取得・検索 SQL では `publisher`、`book_genre`、`book_sales_unit_price_history`、`book_stock`、`store` の結合を維持してください。
- 本検索・取得では `book_sales_unit_price_history` を現在日付で結合し、`BookResponse.salesUnitPrice` を返します。検索 count SQL でも同じ現在単価条件を維持してください。
- 販売単価履歴の追加では `BookSalesUnitPriceHistoryMapper` と `BookCustomMapper` を使い、前履歴の `effective_to` を新履歴の前日に更新し、後続履歴がある場合は新履歴の `effective_to` を後続履歴の前日にします。
- `bookStockList` は `BookStockWithStoreName` と `BookCustomMapper.xml` の `<collection>` で組み立てます。`notNullColumn="bs_id"` と `bs_*` 系の列 alias を維持してください。
- 一覧検索で在庫・店舗を結合する場合は、先に書籍を `limit` / `offset` でページングしてから `book_stock` / `store` を結合してください。在庫行の重複でページング件数が崩れないようにします。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleOrAuthorStartingWithIgnoreCase` と `countByTitleOrAuthorStartingWithIgnoreCase` の両方を更新してください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル/著者条件を付けない方針を維持してください。
- 検索では `limit` / `offset` を使います。offset は `PageCalculator.calculateOffset(page, size)` で算出してください。
- `selectByPrimaryKeyWithWriteLock` は `for update nowait` を使います。ロック失敗時のリトライ方針と合わせて変更してください。
- `BookStockCustomMapper.selectByStoreIdAndBookIdWithWriteLock` は仕入登録時の在庫加算で使います。ロック方針を変更する場合は JPA / Doma / jOOQ 側も揃えてください。
- `BookMapper` / `PublisherMapper` / `BookGenreMapper` / `SupplierMapper` / `StoreMapper` / `PurchaseOrderMapper` / `PurchaseOrderDetailMapper` / `BookStockMapper` / `BookStockMovementMapper` / `BookSalesUnitPriceHistoryMapper` は MyBatis Generator の生成 Mapper です。生成元スキーマとの整合性を維持してください。
- MyBatis Generator の `purchase_invoice` / `purchase_invoice_detail` は、現在 `PurchaseOrderEntity` / `PurchaseOrderDetailEntity`、`PurchaseOrderMapper` / `PurchaseOrderDetailMapper` という生成名です。生成名を変更する場合は Service / converter / XML / テストへの影響を確認してください。
- MyBatis Generator の対象スキーマを変える場合は、`generator-schema.sql` と `generatorConfig.xml` の整合性を確認してください。
- `application.yaml` の `mybatis.mapper-locations` は、MyBatis の XML 読み込みに必要です。不用意に変更しないでください。
- MyBatis TypeHandler は `src/main/resources/mybatis-config.xml` に登録します。`PurchaseInvoiceTypeHandler`、`BookStockMovementTypeHandler`、`BookStockMovementSourceTypeHandler` を変更する場合は `generatorConfig.xml` の `columnOverride` と合わせて確認してください。
- MyBatis 仕入登録では `BookStockMovementMapper` で `book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` の履歴を登録します。

## JPA

- JPA 実装は `BooksOperationServiceJPA`、`BookRepository`、`BookSalesUnitPriceHistoryRepository`、`PublisherRepository`、`BookGenreRepository`、`Book`、`BookSalesUnitPriceHistory`、`Publisher`、`BookGenre` を中心に構成されています。
- 仕入登録は `PurchaseOperationServiceJPA`、`PurchaseOrderRepository`、`PurchaseOrderDetailRepository`、`BookStockRepository`、`BookStockMovementRepository`、`PurchaseDataValidatorJPA`、`PurchaseOperationConverterJPA` を中心に構成されています。
- JPA Entity には `Supplier`、`Store`、`PurchaseOrder`、`PurchaseOrderDetail`、`BookStock`、`BookStockMovement` もあります。
- `application.yaml` では Spring Data JPA repository を無効化し、`application-jpa.yaml` で有効化します。JPA profile の起動確認や設定変更時は、両方の設定を確認してください。
- JPA 側の検索は Spring Data JPA Repository メソッドまたは明示的な `@Query` を優先してください。在庫リストのように1書籍が複数行になる取得では native query と projection の利用を許容します。
- JPA 側の取得・検索レスポンスは `BookRepository.BookWithStockRowProjection` を使います。列を変更する場合は projection、native query、`BookOperationConverterJPA` を揃えてください。
- `BookWithStockRowProjection` は `publisherName`、`genreName`、`isbn`、`salesUnitPrice`、在庫・店舗表示用の行項目を含みます。取得・検索 query では `publisher`、`book_genre`、`book_sales_unit_price_history`、`book_stock`、`store` の結合を維持してください。
- 本検索・取得では `book_sales_unit_price_history` を現在日付で結合し、`BookResponse.salesUnitPrice` を返します。検索 count query でも同じ現在単価条件を維持してください。
- 販売単価履歴の追加では `BookSalesUnitPriceHistoryRepository` を使い、前履歴の `effective_to` を新履歴の前日に更新し、後続履歴がある場合は新履歴の `effective_to` を後続履歴の前日にします。
- JPA の `BookWithStockRowProjection` は1書籍1行ではなく、在庫単位の行を返します。`BookOperationConverterJPA.toResponseFrom(...)` / `toResponse(...)` で書籍単位に集約し、`bookStockList` を組み立ててください。
- JPA 側のページング検索は、取得 query と count query の条件を揃えてください。一覧検索で在庫・店舗を結合する場合は、先に書籍をページングしてから `book_stock` / `store` を結合し、count query は書籍条件のみを数える方針を維持してください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル/著者条件を付けない方針を維持してください。
- JPA 側の更新・削除では `findByIdWithWriteLock` による書き込みロックを維持してください。
- JPA 側の仕入登録では `BookStockRepository.findByStoreIdAndBookIdWithWriteLock` による在庫行ロックを維持してください。
- JPA 側の `publisherId` / `genreId` 参照存在チェックは `PublisherRepository` / `BookGenreRepository` を使う `BookDataValidatorJPA` に集約してください。
- JPA 側の ISBN 一意性チェックと仕入明細 ISBN 参照チェックは `BookRepository.findByIsbn` を使います。
- `PurchaseInvoiceType` は `PurchaseInvoiceTypeConverter` で DB の整数値に変換します。値を変更する場合は DB の CHECK 制約と MyBatis / Doma / jOOQ 側の変換設定も確認してください。
- `BookStockMovementType` / `BookStockMovementSourceType` は JPA の各 converter で DB の整数値に変換します。値を変更する場合は DB の CHECK 制約と MyBatis / Doma / jOOQ 側の変換設定も確認してください。

## ドメイン型と型変換

- 共有ドメイン型は `src/main/java/com/example/demo/data/domain` 配下に置きます。
- `PurchaseInvoiceType` は `PURCHASE(1)`、`RETURN_PURCHASE(2)` を表します。
- `PurchaseInvoiceType` の値を変更する場合は、以下を合わせて確認してください。
  - `generator-schema.sql` の `check_purchase_invoice_type`
  - `PurchaseInvoiceType.of`
  - JPA `PurchaseInvoiceTypeConverter`
  - MyBatis `PurchaseInvoiceTypeHandler`
  - MyBatis Generator の `columnOverride`
  - Doma `@Domain`
  - Doma CodeGen の `entityPropertyClassNames.properties`
  - jOOQ の保存時 `PurchaseInvoiceType#getValue()` / 復元時 `PurchaseInvoiceType.of`
  - `data.sql`
- `BookStockMovementType` は `INITIAL_STOCK(1)`、`PURCHASE(2)`、`SALE(3)`、`RETURN_PURCHASE(4)`、`SALES_RETURN(5)`、`STOCK_ADJUSTMENT(6)`、`STORE_TRANSFER_IN(7)`、`STORE_TRANSFER_OUT(8)` を表します。
- `BookStockMovementSourceType` は `PURCHASE_INVOICE(1)`、`SALES_ORDER(2)`、`STOCK_ADJUSTMENT(3)`、`STORE_TRANSFER(4)` を表します。
- 在庫増減履歴の種別値を変更する場合は、以下を合わせて確認してください。
  - `generator-schema.sql` の `check_book_stock_movement_type` / `check_book_stock_movement_source_type`
  - `BookStockMovementType.of` / `BookStockMovementSourceType.of`
  - JPA `BookStockMovementTypeConverter` / `BookStockMovementSourceTypeConverter`
  - MyBatis `BookStockMovementTypeHandler` / `BookStockMovementSourceTypeHandler`
  - MyBatis Generator の `columnOverride`
  - Doma `@Domain`
  - Doma CodeGen の `entityPropertyClassNames.properties`
  - jOOQ の保存時 `getValue()`
  - `data.sql`

## スキーマ変更時の注意

- `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` テーブルのカラムを変更する場合は、以下の整合性を確認してください。
  - JPA Entity
  - JPA Repository
  - MyBatis Generator の生成 Entity / Mapper
  - Doma CodeGen の生成 Entity / DAO / SQL
  - jOOQ 生成コード
  - 手書き MyBatis Mapper XML
  - 手書き Doma SQL
  - jOOQ DSL / Service / converter / validator
  - JPA `BookWithStockRowProjection`
  - jOOQ `BookWithStockRow`
  - jOOQ `BookSalesUnitPriceHistoryRow`
  - MyBatis Entity / Doma Entity の `BookWithPublisherName`
  - MyBatis / Doma の `BookStockWithStoreName`
  - Doma `BookWithPublisherNameAggregateStrategy`
  - 各永続化方式の `BookDataValidator*`
  - 各永続化方式の `PurchaseDataValidator*`
  - request / response DTO
  - 各永続化方式の `BookOperationConverter*`
  - 各永続化方式の `PurchaseOperationConverter*`
  - `data.sql`
  - `generator-schema.sql`
  - `generatorConfig.xml`
  - `build.gradle` の `generatedTablePattern`
  - `src/main/resources/codegen/entityPropertyClassNames.properties`
  - `src/main/resources/codegen/jooq-codegen-config.xml`
  - `src/main/resources/mybatis-config.xml`
- `generator-schema.sql` は MyBatis Generator、Doma CodeGen、jOOQ CodeGen で使われます。片方だけを想定した変更にしないでください。
- `release_date` は検索条件と DTO に関係します。変更時は API バリデーションと4つの Service 実装を確認してください。
- `isbn` は `book` の一意制約、`@Isbn`、request / response DTO、仕入明細の本参照、各 `BookDataValidator*` / `PurchaseDataValidator*` に関係します。変更時は4つの Service 実装と `GlobalExceptionHandler` の `UniqueConstraintValidationException` も確認してください。
- `publisher_id` は `publisher`、`genre_id` は `book_genre` への外部キーです。変更時は初期データ、生成 Mapper/DAO、外部キー参照チェックを確認してください。
- `publisher_name` / `genre_name` はレスポンス表示項目です。変更時は projection / 表示向け Entity / row / SQL / query / `BookOperationConverter*` を確認してください。
- `salesUnitPrice` は `BookResponse` のレスポンス表示項目で、`book_sales_unit_price_history` の現在有効な履歴から取得します。変更時は request / response DTO、JPA projection、MyBatis / Doma の `BookWithPublisherName`、jOOQ `BookWithStockRow`、取得・検索 SQL / query、検索 count、`BookOperationConverter*` を確認してください。
- 販売単価履歴を追加する API は `BookSalesUnitPriceCreateRequest` を受け取り、`book_sales_unit_price_history` の `book_id,effective_from` 一意制約、`sales_unit_price` の CHECK 制約、`effective_from` / `effective_to` の期間整合性に関係します。変更時は JPA / MyBatis / Doma / jOOQ の前後履歴更新処理を揃えてください。
- `bookStockList` はレスポンス表示項目です。`book_stock` または `store` を変更する場合は、JPA の行 projection、MyBatis の nested collection、Doma の aggregate strategy、jOOQ の `BookWithStockRow`、`BookStockResponse`、`BookOperationConverter*` を確認してください。
- `purchase_invoice_type` は `PurchaseInvoiceType` と DB CHECK 制約に関係します。値追加・変更時は各永続化方式の型変換を確認してください。
- `movement_type` / `source_type` は `BookStockMovementType` / `BookStockMovementSourceType` と DB CHECK 制約に関係します。値追加・変更時は各永続化方式の型変換を確認してください。
- 生成コードの対象テーブルは `build.gradle` の `generatedTablePattern` で管理しています。MyBatis / Doma / jOOQ の生成対象を揃えてください。
- 仕入・在庫・販売単価履歴系の外部キーを変更する場合は、`purchase_invoice.return_purchase_invoice_id`、`purchase_invoice.supplier_id`、`purchase_invoice.receiving_store_id`、`purchase_invoice_detail.purchase_invoice_id`、`purchase_invoice_detail.purchase_invoice_detail_book_id`、`book_stock.book_stock_store_id`、`book_stock.book_stock_book_id`、`book_stock_movement.store_id`、`book_stock_movement.book_id`、`book_sales_unit_price_history.book_id` の整合性を確認してください。
- 検索条件を変更する場合は、JPA / MyBatis / Doma / jOOQ の一覧取得と件数取得が同じ条件になるよう確認してください。

## 生成コマンド

生成物を更新する意図がある場合のみ実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
```

MyBatis Generator と Doma CodeGen はファイルを上書きする可能性があります。実行前後で差分を確認してください。
jOOQ 生成コードは `src/main/java/com/example/demo/jooq/generated` 配下に出力されます。`compileJava` は `generateJooq` に依存しているため、通常のビルド後にも差分を確認してください。
