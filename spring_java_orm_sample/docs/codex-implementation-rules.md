# Codex 実装時の追加規約

## 基本方針

- 変更は依頼内容に必要な範囲へ限定する。
- 無関係なリファクタリング、命名変更、フォーマット変更は避ける。
- 既存の未コミット変更を勝手に戻さない。
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングは避ける。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にする。
- メソッド内の変数宣言には、`final var` を積極的に使う。
- `LocalDateTime.now()` を頻繁に呼ばず、同じ処理内では必要に応じて値を使い回す。
- request DTO や内部 row など不変にしたい class は Lombok の `@Value` を基本にする。
- response DTO は ModelMapper や jOOQ converter から setter で組み立てるため、既存コードと同じく Lombok の `@Data` を基本にする。
- API のリクエスト、レスポンス項目にある `List` には `@NotNull` を付ける。
- 未使用なメソッドであれば削除する。
- JPA / MyBatis / Doma / jOOQ で共通利用する値オブジェクトや列挙型は `src/main/java/com/example/demo/data/domain` 配下に置く。
- 共有ドメイン型を DB に保存する場合は、JPA Converter、MyBatis TypeHandler、Doma `@Domain`、jOOQ 側の値変換の対応を揃える。
- API 関連のクラスは `api`、`api/controller`、`api/log`、`api/request`、`api/response`、`api/validator` の現在の役割分担に合わせて配置する。
- 永続化方式ごとの変換処理は各方式の `converter` package に置く。共通 converter を新設する場合は、JPA / MyBatis / Doma / jOOQ で本当に共有できる責務か確認する。
- SQL で副問合せでの記述が必要な場合、共通テーブル式を使用する。

## API 実装

- API 仕様を変更する場合は、`AuthOperationApi` / `BooksOperationApi` / `PurchaseOperationApi`、各 Controller、request / response DTO、API validator、`GlobalExceptionHandler`、`readme.md` の整合性を確認する。
- OpenAPI 注釈は `AuthOperationApi` / `BooksOperationApi` / `PurchaseOperationApi` に集約する。Controller 側へ重複して追加しない。
- API の入出力には Entity ではなく request / response DTO を使う。
- 認証 API は `/api/auth/login` とし、`LoginRequest` でユーザー名とパスワードを受け取り、`LoginResponse` で `Bearer` token、ユーザー名、有効期限秒数を返す。
- ログイン回数制限のリセット API は `/api/auth/login-rate-limit/reset` とし、Bearer token 必須で 204 を返す。
- Security / JWT / ログイン回数制限の設定を変更する場合は、`SecurityConfig`、`JwtAuthenticationFilter`、`JwtTokenService`、`LoginRateLimitProperties`、`LoginRateLimitService`、`application.yaml` の `app.auth` / `app.auth.login-rate-limit`、`GlobalExceptionHandler`、OpenAPI の `bearerAuth` 設定を合わせて確認する。
- 書籍の取得・検索は未認証で許可し、登録・更新・削除と仕入登録は Bearer token 必須とする現在の認可方針を不用意に変更しない。
- `BookCreateRequest`、`BookUpdateRequest`、`BookResponse` には `releaseDate`、`publisherId`、`genreId` が含まれる。スキーマや永続化層を変更する場合は DTO も確認する。
- `BookResponse` には `publisherName`、`genreName`、`bookStockList` が含まれる。取得・検索系の SQL / query は `publisher`、`book_genre`、`book_stock`、`store` と結合し、永続化方式ごとの `BookOperationConverter*` に渡す値を揃える。
- `bookStockList` の要素は `BookStockResponse` とし、`id`、`bookStockStoreId`、`storeName`、`bookStockQuantity` を返す。
- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱う。`keyword` はタイトルまたは著者の前方一致条件として扱う。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とし、片方だけの指定や From > To は相関バリデーションエラーとして扱う。
- 日付範囲の相関チェックは `BooksOperationApiControllerValidator` に集約する。
- `page` は 0 始まりとする。ページサイズはリクエストパラメータではなく、`application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込む。
- Spring profile や永続化実装の有効化設定を変更する場合は、`application.yaml` と `application-jpa.yaml` の役割を確認する。現在のデフォルト profile は `application.yaml` の `spring.profiles.default: doma`、JPA repository の有効化は `application-jpa.yaml` で扱う。
- 検索 API のレスポンスは `BookPageResponse` とする。検索仕様を変更する場合は `content`、`page`、`size`、`totalElements`、`totalPages` の意味を4つの Service 実装で揃える。
- ページ数と offset の計算は `PageCalculator` を使う。各 Service 実装で同じ計算ロジックを重複させない。
- 仕入登録 API は `/api/purchases/create` とし、`PurchaseInvoiceCreateRequest` で `purchaseInvoiceDate`、`supplierId`、`receivingStoreId`、明細リストを受け取る。
- `PurchaseInvoiceCreateRequest.details` は `@Valid`、`@NotEmpty`、`@NotNull`、`@Size(max = 10)` を維持する。明細の単価は 1〜10000、数量は 1〜1000 の制約を維持する。
- 仕入登録 API のレスポンスは `PurchaseInvoiceResponse` とし、伝票金額、更新日時、バージョン、`PurchaseInvoiceDetailResponse` の明細リストを返す。

## ドメイン型と変換

- `PurchaseInvoiceType` は仕入伝票種別を表す共有ドメイン型として扱う。
- `PurchaseInvoiceType` の値を変更する場合は、DB の `check_purchase_invoice_type` 制約、初期データ、JPA `PurchaseInvoiceTypeConverter`、MyBatis `PurchaseInvoiceTypeHandler`、Doma `@Domain`、jOOQ 側の `getValue()` / `of()` を使った値変換の整合性を確認する。
- MyBatis TypeHandler を追加・変更する場合は、`src/main/resources/mybatis-config.xml` に登録する。
- Doma CodeGen の型解決を変更する場合は、`src/main/resources/codegen/entityPropertyClassNames.properties` と `build.gradle` の `domaCodeGen` 設定を確認する。
- MyBatis Generator の `purchase_invoice` / `purchase_invoice_detail` は、現在 `PurchaseOrderEntity` / `PurchaseOrderDetailEntity`、`PurchaseOrderMapper` / `PurchaseOrderDetailMapper` として生成される。`book_stock` は `BookStockEntity` / `BookStockMapper` として生成される。生成名を変更する場合は XML、テスト、補足ドキュメントを合わせて確認する。

## Service と例外

- `BooksOperationService` は JPA / MyBatis / Doma / jOOQ 共通の Service インターフェースとして扱う。
- Service インターフェースを変更する場合は、JPA / MyBatis / Doma / jOOQ の4実装をすべて確認する。
- `PurchaseOperationService` は JPA / MyBatis / Doma / jOOQ 共通の仕入登録 Service インターフェースとして扱う。
- 現在のデフォルト profile は `doma` であり、通常起動では `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` を使う。実装切り替えに関わる変更では `application.yaml` の `spring.profiles.default`、各実装の `@Profile`、Doma 実装の `@Primary` の扱いを確認する。
- jOOQ 実装は `src/main/java/com/example/demo/jooq` 配下に置く。手書きの SQL / DSL 組み立ては `jooq/dsl`、Service は `jooq/service`、変換は `jooq/converter`、参照存在チェックは `jooq/validator` の役割に合わせる。
- jOOQ 生成コードは `src/main/java/com/example/demo/jooq/generated` 配下に出力する。生成コードを直接編集しない。
- `BookOperationConverterJPA` / `BookOperationConverterMybatis` / `BookOperationConverterDoma` / `BookOperationConverterJooq` は永続化方式ごとの取得結果を `BookResponse` / `BookPageResponse` 用の DTO へ変換する責務に限定する。
- `PurchaseOperationConverterJPA` / `PurchaseOperationConverterMybatis` / `PurchaseOperationConverterDoma` / `PurchaseOperationConverterJooq` は仕入登録用 Entity / row、明細金額、伝票金額、在庫 Entity / row、response DTO への変換を扱う。
- JPA の取得・検索は `BookRepository.BookWithStockRowProjection` の複数行を `BookOperationConverterJPA` で書籍単位に集約する。
- MyBatis / Doma の取得・検索は、各表示向け Entity の `bookStockList` を各 `BookOperationConverter*` で `BookStockResponse` に変換する。
- jOOQ の取得・検索は `BookWithStockRow` の複数行を `BookOperationConverterJooq` で書籍単位に集約する。
- jOOQ の取得・検索・更新 SQL は `BookOperationDsl` / `PurchaseOperationDsl` に集約する。参照存在チェックは `BookDsl` / `BookGenreDsl` / `PublisherDsl` / `StoreDsl` / `SupplierDsl` を使う。Service や validator へ新しい jOOQ クエリを直接追加する場合は、既存の DSL component に置くべき責務か先に確認する。
- DB を読む・更新する Service メソッドには `@Transactional` を付ける。
- `publisherId` は `publisher`、`genreId` は `book_genre` への外部キー。登録・更新時の参照存在チェックは各永続化方式の `BookDataValidator*` に集約する。
- 仕入登録時の `supplierId`、`receivingStoreId`、明細の本 ID の参照存在チェックは各永続化方式の `PurchaseDataValidator*` に集約する。
- 仕入登録では伝票、明細を登録し、JPA は `BookStockRepository.findByStoreIdAndBookIdWithWriteLock`、MyBatis は `BookStockCustomMapper.selectByStoreIdAndBookIdWithWriteLock`、Doma は `BookStockCustomDao.selectByStoreIdAndBookIdWithWriteLock`、jOOQ は `PurchaseOperationDsl` の `forUpdate().noWait()` で在庫行をロックしてから新規作成または数量加算する。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しない。
- 排他ロックを取得して更新・削除する Service メソッドには、必要に応じて `@RetryableOnLockFailure` を付ける。
- 更新競合は `ObjectOptimisticLockingFailureException` / `PessimisticLockingFailureException` と `GlobalExceptionHandler` により HTTP 409 として扱う。
- データなしは `RepositoryDataNotfoundException` と `GlobalExceptionHandler` により HTTP 404 として扱う。
- 相関バリデーションエラーは `CorrelationValidationFailureException` と `GlobalExceptionHandler` により HTTP 400 として扱う。
- 外部キー参照先なしは `ForeignKeyReferenceNotFoundException` と `GlobalExceptionHandler` により HTTP 400 として扱う。
- ログイン回数制限超過は `LoginRateLimitExceededException` と `GlobalExceptionHandler` により HTTP 429 として扱う。
- Bean Validation のパラメータ違反は `ConstraintViolationException` と `GlobalExceptionHandler` により HTTP 400 として扱う。

## テスト

コード変更後は、基本的に以下を実行する。

```shell
./gradlew test
```

対象が限定されている場合でも、関連するテストを確認する。

- API 相関バリデーション: `BooksOperationApiControllerValidatorTest`
- API Controller: `BooksOperationApiControllerTest`
- 認証 API / Security: `AuthOperationApiControllerTest`、`AuthOperationApiLoginRateLimitTest`
- ログイン回数制限: `LoginRateLimitServiceTest`
- 例外ハンドリング: `GlobalExceptionHandlerTest`
- ページ計算: `PageCalculatorTest`
- JPA 実装: `BooksOperationServiceJPATest`
- JPA 仕入実装: `PurchaseOperationServiceJPATest`
- MyBatis 実装: `BooksOperationServiceMybatisTest`
- MyBatis 仕入実装: `PurchaseOperationServiceMybatisTest`
- Doma 実装: `BooksOperationServiceDomaTest`
- Doma 仕入実装: `PurchaseOperationServiceDomaTest`
- Doma 仕入データバリデーション: `PurchaseDataValidatorDomaTest`
- 仕入 API Controller: `PurchaseOperationApiControllerTest`
- jOOQ 実装: `BooksOperationServiceJooqTest`
- jOOQ 仕入実装: `PurchaseOperationServiceJooqTest`
- ロック失敗リトライ: `RetryableOnLockFailureTest`、`LockFailureRetryTest`
- 行ロック関連: `BookRowLock`

API、Security、DB 設定、JPA / MyBatis / Doma / jOOQ の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認する。
