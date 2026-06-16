# Codex 実装時の追加規約

## 基本方針

- 変更は依頼内容に必要な範囲へ限定する。
- 無関係なリファクタリング、命名変更、フォーマット変更は避ける。
- 既存の未コミット変更を勝手に戻さない。
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングは避ける。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にする。
- メソッド内の変数宣言には、`final var` を積極的に使う。
- `LocalDateTime.now()` を頻繁に呼ばず、同じ処理内では必要に応じて値を使い回す。
- record にしたいところでは、Lombok の `@Value` を付けた class にする。
- API のリクエスト、レスポンス項目にある `List` には `@NotNull` を付ける。
- 未使用なメソッドであれば削除する。
- JPA / MyBatis / Doma で共通利用する値オブジェクトや列挙型は `src/main/java/com/example/demo/data/domain` 配下に置く。
- 共有ドメイン型を DB に保存する場合は、JPA Converter、MyBatis TypeHandler、Doma `@Domain` の対応を揃える。
- SQLで副問合せでの記述が必要な場合、共通テーブル式を使用する。

## API 実装

- API 仕様を変更する場合は、`BooksOperationApi`、`BooksOperationApiController`、request / response DTO、`BooksOperationApiControllerValidator`、`GlobalExceptionHandler`、`readme.md` の整合性を確認する。
- OpenAPI 注釈は `BooksOperationApi` に集約する。Controller 側へ重複して追加しない。
- API の入出力には Entity ではなく request / response DTO を使う。
- `BookCreateRequest`、`BookUpdateRequest`、`BookResponse` には `releaseDate`、`publisherId`、`genreId` が含まれる。スキーマや永続化層を変更する場合は DTO も確認する。
- `BookResponse` には `publisherName` と `genreName` が含まれる。取得・検索系の SQL / JPQL は `publisher`、`book_genre` と結合し、`BookConverter` に渡す値を揃える。
- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱う。`keyword` はタイトルまたは著者の前方一致条件として扱う。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とし、片方だけの指定や From > To は相関バリデーションエラーとして扱う。
- 日付範囲の相関チェックは `BooksOperationApiControllerValidator` に集約する。
- `page` は 0 始まりとする。ページサイズはリクエストパラメータではなく、`application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込む。
- 検索 API のレスポンスは `BookPageResponse` とする。検索仕様を変更する場合は `content`、`page`、`size`、`totalElements`、`totalPages` の意味を3つの Service 実装で揃える。
- ページ数と offset の計算は `PageCalculator` を使う。各 Service 実装で同じ計算ロジックを重複させない。

## ドメイン型と変換

- `PurchaseOrderType` は仕入伝票種別を表す共有ドメイン型として扱う。
- `PurchaseOrderType` の値を変更する場合は、DB の CHECK 制約、初期データ、JPA `PurchaseOrderTypeConverter`、MyBatis `PurchaseOrderTypeHandler`、Doma `@Domain` の整合性を確認する。
- MyBatis TypeHandler を追加・変更する場合は、`src/main/resources/mybatis-config.xml` に登録する。
- Doma CodeGen の型解決を変更する場合は、`src/main/resources/codegen/entityPropertyClassNames.properties` と `build.gradle` の `domaCodeGen` 設定を確認する。

## Service と例外

- `BooksOperationService` は JPA / MyBatis / Doma 共通の Service インターフェースとして扱う。
- Service インターフェースを変更する場合は、JPA / MyBatis / Doma の3実装をすべて確認する。
- DB を読む・更新する Service メソッドには `@Transactional` を付ける。
- `publisherId` は `publisher`、`genreId` は `book_genre` への外部キー。登録・更新時の参照存在チェックは各永続化方式の `BookDataValidator*` に集約する。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しない。
- 排他ロックを取得して更新・削除する Service メソッドには、必要に応じて `@RetryableOnLockFailure` を付ける。
- 更新競合は `ObjectOptimisticLockingFailureException` / `PessimisticLockingFailureException` と `GlobalExceptionHandler` により HTTP 409 として扱う。
- データなしは `RepositoryDataNotfoundException` と `GlobalExceptionHandler` により HTTP 404 として扱う。
- 相関バリデーションエラーは `CorrelationValidationFailureException` と `GlobalExceptionHandler` により HTTP 400 として扱う。
- 外部キー参照先なしは `ForeignKeyReferenceNotFoundException` と `GlobalExceptionHandler` により HTTP 400 として扱う。
- Bean Validation のパラメータ違反は `ConstraintViolationException` と `GlobalExceptionHandler` により HTTP 400 として扱う。

## テスト

コード変更後は、基本的に以下を実行する。

```shell
./gradlew test
```

対象が限定されている場合でも、関連するテストを確認する。

- API 相関バリデーション: `BooksOperationApiControllerValidatorTest`
- API Controller: `BooksOperationApiControllerTest`
- 例外ハンドリング: `GlobalExceptionHandlerTest`
- ページ計算: `PageCalculatorTest`
- JPA 実装: `BooksOperationServiceJPATest`
- MyBatis 実装: `BooksOperationServiceMybatisTest`
- Doma 実装: `BooksOperationServiceDomaTest`
- Doma 生成 DAO: `BookDaoTest`
- Doma 生成 Publisher DAO: `PublisherDaoTest`
- Doma 生成 BookGenre DAO: `BookGenreDaoTest`
- Doma 生成 Supplier DAO: `SupplierDaoTest`
- Doma 生成 Store DAO: `StoreDaoTest`
- Doma 生成 PurchaseOrder DAO: `PurchaseOrderDaoTest`
- Doma 生成 PurchaseOrderDetail DAO: `PurchaseOrderDetailDaoTest`
- Doma 生成 BookStock DAO: `BookStockDaoTest`
- ロック失敗リトライ: `RetryableOnLockFailureTest`、`LockFailureRetryTest`
- 行ロック関連: `BookRowLock`

API、Security、DB 設定、JPA / MyBatis / Doma の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認する。
