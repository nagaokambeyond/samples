# 永続化実装メモ

## 全体方針

- 現在のデフォルト実装は `BooksOperationServiceDoma` です。
- `BooksOperationService` は JPA / MyBatis / Doma 共通の Service インターフェースです。
- `BookDataValidatorJPA` / `BookDataValidatorMybatis` / `BookDataValidatorDoma` は永続化方式ごとのデータバリデーションを扱います。
- `BookConverter` は projection / 表示向け Entity から response DTO への変換を扱います。
- `book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id` を参照します。
- 現在のスキーマには `publisher`、`book_genre`、`book`、`supplier`、`store`、`purchase_order`、`purchase_order_detail`、`book_stock` があります。
- `purchase_order.purchase_order_type` は `PurchaseOrderType` で扱います。JPA / MyBatis / Doma の型変換設定を揃えてください。
- Service 内のメソッドで排他をかけてデータを取得する箇所があれば、メソッドに `@RetryableOnLockFailure` を付けてリトライします。
- ページング検索の offset と totalPages は `PageCalculator` を使って計算します。各 Service 実装で計算式を重複させないでください。
- JPA / MyBatis / Doma のうち1つの実装を変更する場合でも、他の実装で同じ仕様が必要か確認してください。

## Doma

- `src/main/java/com/example/demo/doma/generator` 配下は Doma CodeGen の生成コードです。手作業での編集は避けてください。
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao` 配下の SQL も生成物として扱ってください。
- 手書き SQL は `BookCustomDao` と `src/main/resources/META-INF/com/example/demo/doma/dao` 配下に追加してください。
- Doma の DAO メソッドを追加する場合は、対応する SQL ファイルのパスとメソッド名を揃えてください。
- `BookWithPublisherName` は Doma 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は SQL と `BookConverter` も更新してください。
- `BookWithPublisherName` は `publisherName` と `genreName` を含みます。取得・検索 SQL では `publisher` と `book_genre` の結合を維持してください。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleContainingIgnoreCase.sql` と `countByTitleContainingIgnoreCase.sql` の両方を更新してください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル条件を付けない方針を維持してください。
- 検索では `limit` / `offset` を使います。offset は `PageCalculator.calculateOffset(page, size)` で算出してください。
- `selectByIdWithWriteLock.sql` は `for update nowait` を使います。ロック失敗時のリトライ方針と合わせて変更してください。
- `BookDao` / `PublisherDao` / `BookGenreDao` / `SupplierDao` / `StoreDao` / `PurchaseOrderDao` / `PurchaseOrderDetailDao` / `BookStockDao` は Doma CodeGen の生成 DAO です。生成元スキーマとの整合性を維持してください。
- Doma CodeGen の対象スキーマを変える場合は、`generator-schema.sql` と `build.gradle` の `domaCodeGen` 設定の整合性を確認してください。
- Doma CodeGen の対象テーブルは `build.gradle` の `tableNamePattern` で管理しています。テーブル追加・削除時はここも更新してください。
- Doma CodeGen の型解決は `src/main/resources/codegen/entityPropertyClassNames.properties` も参照します。`PurchaseOrderType` などのドメイン型を追加・変更する場合はこのファイルも確認してください。
- Doma 側の更新・削除では、Doma の楽観ロック例外を `ObjectOptimisticLockingFailureException` に変換する既存方針を維持してください。

## MyBatis

- `src/main/java/com/example/demo/mybatis/generator` 配下は MyBatis Generator の生成コードです。手作業での編集は避けてください。
- `src/main/resources/com/example/demo/mybatis/generator` 配下の XML も生成物として扱ってください。
- 手書き SQL は `BookCustomMapper` と `BookCustomMapper.xml` に追加してください。
- `BookWithPublisherName` は MyBatis 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は resultMap と `BookConverter` も更新してください。
- `BookWithPublisherName` は `publisherName` と `genreName` を含みます。取得・検索 SQL では `publisher` と `book_genre` の結合を維持してください。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleContainingIgnoreCase` と `countByTitleContainingIgnoreCase` の両方を更新してください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル条件を付けない方針を維持してください。
- 検索では `limit` / `offset` を使います。offset は `PageCalculator.calculateOffset(page, size)` で算出してください。
- `selectByPrimaryKeyWithWriteLock` は `for update nowait` を使います。ロック失敗時のリトライ方針と合わせて変更してください。
- `BookMapper` / `PublisherMapper` / `BookGenreMapper` / `SupplierMapper` / `StoreMapper` / `PurchaseOrderMapper` / `PurchaseOrderDetailMapper` は MyBatis Generator の生成 Mapper です。生成元スキーマとの整合性を維持してください。
- 現在 `book_stock` は Doma CodeGen / JPA Entity 側にはありますが、MyBatis Generator の `generatorConfig.xml` には明示されていません。MyBatis 側でも扱う場合は `generatorConfig.xml` に対象テーブルを追加してください。
- MyBatis Generator の対象スキーマを変える場合は、`generator-schema.sql` と `generatorConfig.xml` の整合性を確認してください。
- `application.yaml` の `mybatis.mapper-locations` は、MyBatis の XML 読み込みに必要です。不用意に変更しないでください。
- MyBatis TypeHandler は `src/main/resources/mybatis-config.xml` に登録します。`PurchaseOrderTypeHandler` を変更する場合は `generatorConfig.xml` の `columnOverride` と合わせて確認してください。

## JPA

- JPA 実装は `BooksOperationServiceJPA`、`BookRepository`、`PublisherRepository`、`BookGenreRepository`、`Book`、`Publisher`、`BookGenre` を中心に構成されています。
- JPA Entity には `Supplier`、`Store`、`PurchaseOrder`、`PurchaseOrderDetail`、`BookStock` もあります。これらを Service/API から扱う場合は Repository、Validator、テストの追加を検討してください。
- JPA 側の検索は Spring Data JPA Repository メソッドまたは明示的な `@Query` を優先してください。
- JPA 側の取得・検索レスポンスは `BookWithPublisherNameProjection` を使います。列を変更する場合は projection、JPQL、`BookConverter` を揃えてください。
- `BookWithPublisherNameProjection` は `publisherName` と `genreName` を含みます。取得・検索 JPQL では `Publisher` と `BookGenre` の結合を維持してください。
- JPA 側のページング検索は `Pageable` と `countQuery` を使います。検索条件を変更する場合は取得クエリと countQuery の条件を揃えてください。
- 検索条件の `keyword` は任意です。未指定または空文字の場合はタイトル条件を付けない方針を維持してください。
- JPA 側の更新・削除では `findByIdWithWriteLock` による書き込みロックを維持してください。
- JPA 側の `publisherId` / `genreId` 参照存在チェックは `PublisherRepository` / `BookGenreRepository` を使う `BookDataValidatorJPA` に集約してください。
- `PurchaseOrderType` は `PurchaseOrderTypeConverter` で DB の整数値に変換します。値を変更する場合は DB の CHECK 制約と MyBatis / Doma 側の変換設定も確認してください。

## ドメイン型と型変換

- 共有ドメイン型は `src/main/java/com/example/demo/data/domain` 配下に置きます。
- `PurchaseOrderType` は `PURCHASE(1)`、`RETURN_PURCHASE(2)` を表します。
- `PurchaseOrderType` の値を変更する場合は、以下を合わせて確認してください。
  - `generator-schema.sql` の `check_purchase_order_type`
  - `PurchaseOrderType.of`
  - JPA `PurchaseOrderTypeConverter`
  - MyBatis `PurchaseOrderTypeHandler`
  - MyBatis Generator の `columnOverride`
  - Doma `@Domain`
  - Doma CodeGen の `entityPropertyClassNames.properties`
  - `data.sql`

## スキーマ変更時の注意

- `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_order`、`purchase_order_detail`、`book_stock` テーブルのカラムを変更する場合は、以下の整合性を確認してください。
  - JPA Entity
  - JPA Repository
  - MyBatis Generator の生成 Entity / Mapper
  - Doma CodeGen の生成 Entity / DAO / SQL
  - 手書き MyBatis Mapper XML
  - 手書き Doma SQL
  - JPA projection / MyBatis Entity / Doma Entity の `BookWithPublisherName`
  - 各永続化方式の `BookDataValidator*`
  - request / response DTO
  - `BookConverter`
  - `data.sql`
  - `generator-schema.sql`
  - `generatorConfig.xml`
  - `build.gradle` の `domaCodeGen.tableNamePattern`
  - `src/main/resources/codegen/entityPropertyClassNames.properties`
  - `src/main/resources/mybatis-config.xml`
- `generator-schema.sql` は MyBatis Generator と Doma CodeGen の両方で使われます。片方だけを想定した変更にしないでください。
- `release_date` は検索条件と DTO に関係します。変更時は API バリデーションと3つの Service 実装を確認してください。
- `publisher_id` は `publisher`、`genre_id` は `book_genre` への外部キーです。変更時は初期データ、生成 Mapper/DAO、外部キー参照チェックを確認してください。
- `publisher_name` / `genre_name` はレスポンス表示項目です。変更時は projection / 表示向け Entity / SQL / `BookConverter` を確認してください。
- `purchase_order_type` は `PurchaseOrderType` と DB CHECK 制約に関係します。値追加・変更時は各永続化方式の型変換を確認してください。
- 仕入・在庫系の外部キーを変更する場合は、`purchase_order.return_purchase_order_id`、`purchase_order.supplier_id`、`purchase_order.receiving_store_id`、`purchase_order_detail.purchase_order_id`、`purchase_order_detail.purchase_order_detail_book_id`、`book_stock.book_stock_store_id`、`book_stock.book_stock_book_id` の整合性を確認してください。
- 検索条件を変更する場合は、JPA / MyBatis / Doma の一覧取得と件数取得が同じ条件になるよう確認してください。

## 生成コマンド

生成物を更新する意図がある場合のみ実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
```

MyBatis Generator と Doma CodeGen はファイルを上書きする可能性があります。実行前後で差分を確認してください。
