# AGENTS.md

## プロジェクト概要

このプロジェクトは、Java 21 / Gradle / Spring Boot を使った書籍・仕入管理 API のサンプルアプリケーションです。

主な技術要素は以下です。

- Spring Web
- Spring Data JPA
- MyBatis
- MyBatis Generator
- Doma
- Doma CodeGen
- jOOQ
- H2 Database
- Spring Validation
- Spring Security
- springdoc-openapi
- ModelMapper
- OpenAPI Generator
- GraalVM Native Build Tools
- BootUI（開発時のみ）

現在の主な明示バージョンは、Spring Boot 4.1.1、GraalVM Native Build Tools 1.1.6、OpenAPI Generator 7.24.0、MyBatis Spring Boot Starter 4.1.0、Doma 3.14.0 / Doma Spring Boot Starter 3.0.0、springdoc-openapi 3.0.3、BootUI 1.13.1、H2 2.4.240、jackson-databind-nullable 0.2.11 です。Spring Boot BOM 管理下の依存は、原則として明示バージョンを追加せず BOM に従います。

API は `/api/auth`、`/api/books`、`/api/purchases` 配下にあり、H2 のインメモリデータベースを使用します。`/api/books/openbd` では OpenBD API クライアントを使って外部書誌情報を取得します。初期データは `src/main/resources/data.sql` で投入されます。

現在の主なドメインは `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` です。各テーブルの主キーはテーブル単位の `*_seq` シーケンスで採番します。`book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id`、`book_sales_unit_price_history.book_id` は `book.id` を参照します。`book.isbn` は 13 桁の一意な ISBN として扱います。検索 API はページングされ、出版社名・ジャンル名・ISBN・現在販売単価・在庫リストを含む `BookPageResponse` を返します。

## 追加の作業規約

Codex は作業内容に応じて以下も確認してください。

- 実装・修正・レビュー全般: `docs/codex-implementation-rules.md`
- JPA / MyBatis / Doma / jOOQ / DB スキーマ / 生成コードに関わる変更: `docs/persistence-implementation-notes.md`

`AGENTS.md` と補足ドキュメントが矛盾する場合は、より具体的な指示を優先してください。

## よく使うコマンド

Gradle Wrapper を使用してください。

```shell
./gradlew test
./gradlew bootRun
./gradlew build
```

`./gradlew test` は通常の JVM テストとして実行します。`build.gradle` の `test` タスクでは、Spring Boot AOT プラグインが `test` の runtime classpath に追加する AOT テスト生成物を除外するため、`sourceSets.test.output`、`sourceSets.main.output`、`configurations.testRuntimeClasspath` から classpath を明示的に構成しています。これにより通常のテスト実行時には `processTestAot` / `compileAotTestJava` / `aotTestClasses` を実行しません。この classpath 設定を削除したり `sourceSets.test.runtimeClasspath` へ戻したりする場合は、テスト実行時間とタスクグラフへの影響を確認してください。

アプリ起動後に確認できる画面:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`
- Scalar: `http://localhost:8080/scalar`
- H2 Console: `http://localhost:8080/h2-console`
- BootUI: `http://localhost:8080/bootui`
- Actuator Health: `http://localhost:8080/actuator/health`

負荷テストを実行する場合は、`bootRun` ではなくパッケージ化した JAR を `loadtest` profile 付きで起動してください。`loadtest` profile は、測定結果に影響する API リクエスト／レスポンスログ、SQL ログ、H2 Console、DevTools の再起動機能、API ドキュメント用エンドポイントを無効化し、Hikari と Tomcat の MBean を有効化します。

```shell
./gradlew bootJar
mkdir -p build/load-test
java -XX:StartFlightRecording=filename=build/load-test/doma.jfr,dumponexit=true,settings=profile \
  -jar build/libs/demo-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=doma,loadtest \
  --server.port=18080
```

負荷テストツールは `docker/performance-tests/compose.yaml` で固定した公式 k6 Docker イメージを使います。リポジトリルートから実行してください。

```shell
docker compose -f docker/performance-tests/compose.yaml pull
BASE_URL=http://host.docker.internal:18080 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run --summary-export=/results/capacity-ramp.json /work/scripts/capacity-ramp.js
```

ローカルでは Docker コンテナからホスト側 Spring Boot へ接続するため、`BASE_URL` の既定値は `http://host.docker.internal:18080` です。正式な最大性能測定では、API と k6 を別 PC または別 VM で動かし、API と負荷生成機が CPU、メモリ、ネットワークを奪い合わない構成にしてください。

生成物を更新する意図がある場合のみ、以下を実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
./gradlew syncOpenBdGeneratedSources
```

`compileJava` は `generateJooq` と `syncOpenBdGeneratedSources` に依存しているため、通常のビルド時にも jOOQ 生成コードや OpenBD 生成コードが更新される可能性があります。
Spring Boot や OpenAPI Generator などの依存バージョンを更新した場合も、生成コード差分が出る可能性があります。差分が生成ツールのバージョン表記や未使用 import 削除など機械的な内容か確認してください。

ネイティブイメージを確認する場合は、Oracle GraalVM 25 を使用する以下のタスクを実行してください。通常の Java コンパイル用 toolchain は Java 21 のままです。

```shell
./gradlew processAot
./gradlew nativeCompile
```

`processAot` と生成したネイティブ実行ファイルは `doma,native` profile で動作します。`nativeCompile` は実行時間が長いため、ネイティブ対応に関わる変更時に実行してください。ネイティブテストが必要な場合は `./gradlew nativeTest` を使用します。`nativeTest` のタスクグラフには `processTestAot` / `compileAotTestJava` / `aotTestClasses` が含まれ、通常の `./gradlew test` とは分離されています。

## ディレクトリ構成

- `src/main/java/com/example/demo/api`: API インターフェース
- `src/main/java/com/example/demo/api/annotation`: API 入力用の独自 Bean Validation annotation
- `src/main/java/com/example/demo/api/controller`: Controller
- `src/main/java/com/example/demo/api/log`: API 横断ログ
- `src/main/java/com/example/demo/api/request`: request DTO
- `src/main/java/com/example/demo/api/response`: response DTO
- `src/main/java/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/java/com/example/demo/data/domain`: JPA / MyBatis / Doma / jOOQ で共有するドメイン型
- `src/main/java/com/example/demo/service`: アプリケーション共通の Service インターフェース
- `src/main/java/com/example/demo/util`: 共通ユーティリティ。ページ計算、例外ハンドリング補助を含みます。
- `src/main/java/com/example/demo/exception`: アプリケーション例外
- `src/main/java/com/example/demo/config`: Spring 設定、Security / JWT、ログイン回数制限、例外ハンドリング、検索設定、ロック失敗リトライ設定、ネイティブイメージ用 runtime hints
- `src/main/java/com/example/demo/openbd`: OpenBD API クライアント設定と OpenAPI Generator 生成コード
- `src/main/java/com/example/demo/openbd/config`: OpenBD API クライアントの Spring 設定、接続先設定
- `src/main/java/com/example/demo/openbd/generated`: OpenAPI Generator で生成した OpenBD API クライアントコード
- `src/main/java/com/example/demo/jpa`: JPA 実装。設定、Entity、Repository、Service、converter、型変換、データバリデーションを含みます。
- `src/main/java/com/example/demo/jpa/config`: JPA profile 固有の Spring 設定
- `src/main/java/com/example/demo/mybatis`: MyBatis 実装。手書き Mapper / 表示向け Entity / Service / converter / TypeHandler / データバリデーションと、Generator 生成コードを含みます。
- `src/main/java/com/example/demo/doma`: Doma 実装。手書き DAO / 表示向け Entity / AggregateStrategy / Service / converter / データバリデーションと、CodeGen 生成コードを含みます。
- `src/main/java/com/example/demo/jooq`: jOOQ 実装。Service、DSL component、converter、validator、表示向け row、jOOQ 生成コードを含みます。
- `src/main/java/com/example/demo/jooq/dsl`: jOOQ の手書き SQL / DSL 組み立て
- `src/main/java/com/example/demo/jooq/generated`: jOOQ 生成コード
- `src/main/resources/application.yaml`: アプリケーション設定
- `src/main/resources/application-jpa.yaml`: JPA profile 用の Spring Data JPA Repository 有効化設定
- `src/main/resources/application-loadtest.yaml`: 負荷テスト用設定。ログ、H2 Console、DevTools 再起動、API ドキュメント用エンドポイントを抑制し、JWT 有効期限と監視用 MBean を調整します。
- `src/main/resources/application-dast.yaml`: OWASP ZAP API 脆弱性診断用設定。OpenAPI 仕様取得を有効にし、H2 Console、Swagger UI、Scalar、詳細ログ、ログイン回数制限の影響を診断向けに調整します。
- `src/main/resources/application-native.yaml`: ネイティブ実行用の自動構成除外、H2、SQL 初期化設定
- `src/main/resources/mybatis-config.xml`: MyBatis TypeHandler 設定
- `src/main/resources/codegen`: Doma CodeGen / jOOQ CodeGen 補助設定
- `src/main/resources/openapi/openbd_api_spec.yaml`: OpenBD API クライアント生成用 OpenAPI 仕様
- `src/main/resources/com/example/demo/mybatis/mapper`: 手書き MyBatis SQL
- `src/main/resources/com/example/demo/mybatis/generator/mapper`: MyBatis Generator 生成 SQL
- `src/main/resources/META-INF/com/example/demo/doma/dao`: 手書き Doma SQL
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao`: Doma CodeGen 生成 SQL
- `src/main/resources/data.sql`: 起動時の初期データ
- `src/main/resources/generator-schema.sql`: MyBatis Generator / Doma CodeGen / jOOQ CodeGen 用スキーマ
- `src/test/java/com/example/demo`: アプリケーション、API、永続化実装、例外ハンドリングのテスト
- `src/test/java/com/example/demo/jpa/validator`: JPA データバリデーションのテスト
- `src/test/java/com/example/demo/mybatis/validator`: MyBatis データバリデーションのテスト
- `src/test/java/com/example/demo/doma/validator`: Doma データバリデーションのテスト
- `src/test/java/com/example/demo/jooq/validator`: jOOQ データバリデーションのテスト
- `src/test/java/com/example/demo/openbd/config`: OpenBD API クライアント設定のテスト
- `src/test/java/com/example/demo/util`: 共通ユーティリティのテスト
- `docker/performance-tests`: k6 による API 負荷テスト。Docker Compose 設定、テストスクリプト、結果出力先を含みます。
- `docker/security-tests`: OWASP ZAP による API 脆弱性診断。Docker Compose 設定、Automation Framework YAML、実行スクリプト、結果出力先を含みます。

## 重要な設計方針

- `BooksOperationApi` は API 定義と OpenAPI 注釈を扱います。
- `OpenBdBooksApi` は OpenBD 書誌取得 API 定義と OpenAPI 注釈を扱います。
- `PurchaseOperationApi` は仕入 API 定義と OpenAPI 注釈を扱います。
- `AuthOperationApi` は認証 API 定義と OpenAPI 注釈を扱います。
- `BooksOperationApiController` は `BooksOperationApi` を実装し、Service に処理を委譲します。
- `OpenBdBooksApiController` は `OpenBdBooksApi` を実装し、OpenAPI Generator 生成の `BooksApi` で OpenBD API を呼び出し、`ModelMapper` で `OpenBdBookResponse` に変換します。
- `PurchaseOperationApiController` は `PurchaseOperationApi` を実装し、Service に処理を委譲します。
- `AuthOperationApiController` は `AuthOperationApi` を実装し、`LoginRateLimitService`、`AuthenticationManager`、`JwtTokenService` でログイン回数制限、認証、Bearer token 発行、ログイン回数制限リセットを扱います。
- `BooksOperationApiControllerValidator` は API 入力の相関バリデーションを扱います。
- `BooksOperationService` は JPA / MyBatis / Doma / jOOQ 共通の Service インターフェースです。
- `PurchaseOperationService` は JPA / MyBatis / Doma / jOOQ 共通の仕入登録 Service インターフェースです。
- `PageCalculator` は `src/main/java/com/example/demo/util` 配下でページ数と offset の計算を扱います。`ExceptionHandlerUtil` は `GlobalExceptionHandler` の validation error 生成補助を扱います。
- `SearchProperties` は検索 API のページサイズ設定を扱います。
- `OpenBdClientConfig` は OpenAPI Generator 生成の `ApiClient`、`BooksApi`、`MetadataApi` Bean を構成します。接続先は `OpenBdProperties` と `application.yaml` の `openbd.base-url` で管理します。
- `NativeRuntimeHints` は request / response DTO、Doma Entity、OpenBD 生成 DTO などのリフレクション情報と、Doma SQL / `generator-schema.sql` のリソース情報を AOT に登録します。対象型や実行時リソースを追加した場合は runtime hints も確認してください。
- `DemoApplication` は `@ImportRuntimeHints` で `NativeRuntimeHints` を読み込みます。JPA auditing は `jpa` profile の `JpaAuditingConfig` でのみ有効化します。
- BootUI は `build.gradle` の `developmentOnly` dependency として導入し、開発時のアプリケーション確認に使用します。`/bootui` と `/bootui/**` は `SecurityConfig` で Spring Security / JWT filter の対象外です。
- `BookOperationConverterJPA` / `BookOperationConverterMybatis` / `BookOperationConverterDoma` / `BookOperationConverterJooq` は本情報、`book_sales_unit_price_history` 由来の現在販売単価、`book_stock` / `store` 由来の在庫表示情報を `BookResponse` / `BookStockResponse` に変換します。
- JPA の取得・検索は `BookRepository.BookWithStockRowProjection` の在庫行を `BookOperationConverterJPA` で書籍単位に集約します。
- MyBatis の取得・検索は `BookWithPublisherName` と `BookStockWithStoreName` を `BookCustomMapper.xml` の nested collection で組み立てます。
- Doma の取得・検索は `BookWithPublisherNameAggregateStrategy` で `bookStockList` を集約します。
- jOOQ の取得・検索は `BookWithStockRow` の在庫行を `BookOperationConverterJooq` で書籍単位に集約します。
- 本の登録時は `BookCreateRequest.salesUnitPrice` から `book_sales_unit_price_history` の初期履歴を作成します。販売単価履歴追加 API は前後履歴の `effective_to` を調整し、現在単価は有効期間が現在日に一致する履歴から返します。
- jOOQ の手書き SQL / DSL 組み立ては `BookOperationDsl` / `PurchaseOperationDsl` に集約します。参照存在チェックは `BookDsl` / `BookGenreDsl` / `PublisherDsl` / `StoreDsl` / `SupplierDsl` を使います。
- 仕入登録は JPA / MyBatis / Doma / jOOQ の各 `PurchaseOperationService*` が `PurchaseInvoice` / `PurchaseInvoiceDetail` 相当のデータを登録し、在庫をロックして新規作成または数量加算し、`book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` の在庫増減履歴を登録します。
- `PurchaseInvoiceType` は仕入伝票種別を表す共有ドメイン型です。JPA は `PurchaseInvoiceTypeConverter`、MyBatis は `PurchaseInvoiceTypeHandler`、Doma は `@Domain`、jOOQ は converter / Service 側の値変換で扱います。
- `BookStockMovementType` と `BookStockMovementSourceType` は在庫増減履歴の共有ドメイン型です。JPA は converter、MyBatis は TypeHandler、Doma は `@Domain`、jOOQ は Service / DSL 側の値変換で扱います。
- 全テーブルの主キーは `generator-schema.sql` で定義した `publisher_seq`、`book_genre_seq`、`book_seq`、`supplier_seq`、`store_seq`、`purchase_invoice_seq`、`purchase_invoice_detail_seq`、`book_sales_unit_price_history_seq`、`book_stock_seq`、`book_stock_movement_seq` から採番します。`max(id) + 1` や IDENTITY 採番へ戻さないでください。
- JPA は `@SequenceGenerator(allocationSize = 1)`、MyBatis は `generatorConfig.xml` と Mapper XML の `selectKey BEFORE`、Doma は `@SequenceGenerator(allocationSize = 1)`、jOOQ は `BookOperationDsl` / `PurchaseOperationDsl` のシーケンス取得処理を使います。採番方式を変更する場合は4実装と生成設定を揃えてください。
- `data.sql` は依存関係を考慮した順序で既存データを削除して初期データを投入し、最後に各シーケンスを初期データの次の値へ再設定します。初期データの ID を変更した場合は `ALTER SEQUENCE ... RESTART WITH` も更新してください。
- MyBatis Generator の `purchase_invoice` / `purchase_invoice_detail` は、現在 `PurchaseOrderEntity` / `PurchaseOrderDetailEntity`、`PurchaseOrderMapper` / `PurchaseOrderDetailMapper` という生成名です。`book_stock` は `BookStockEntity` / `BookStockMapper`、`book_stock_movement` は `BookStockMovementEntity` / `BookStockMovementMapper`、`book_sales_unit_price_history` は `BookSalesUnitPriceHistoryEntity` / `BookSalesUnitPriceHistoryMapper` として生成されます。生成名を変更する場合は影響範囲を確認してください。
- 現在のデフォルト profile は `application.yaml` の `spring.profiles.default: doma` です。通常起動では `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` が使われます。
- ネイティブイメージの AOT 処理と実行には `doma,native` profile を使用します。`native` profile では MyBatis、JPA、jOOQ の自動構成と H2 Console を無効化し、`generator-schema.sql` でスキーマを初期化します。
- 通常の `test` タスクは AOT テスト生成物を classpath に含めません。AOT テスト生成物は `nativeTest` の経路でのみ使用し、通常の JVM テストへ再接続しないでください。
- 認証設定は `application.yaml` の `app.auth` 配下で管理します。`app.auth.login-rate-limit` はログインの日次回数制限を扱います。`/api/auth/login`、書籍の取得・検索、OpenBD 書誌取得は公開され、それ以外の API は Bearer token が必要です。開発支援画面の `/bootui` と `/bootui/**` は認証対象外です。
- Actuator の Web 公開は `application.yaml` の `management.endpoints.web.exposure.include: health` で `/actuator/health` のみに限定します。`/actuator/env` など設定情報を返すエンドポイントを不用意に公開しないでください。
- API の入出力には Entity ではなく request / response DTO を使ってください。
- `BookCreateRequest` / `BookUpdateRequest` / `BookResponse` には `isbn` が含まれます。ISBN は `@Isbn` で 13 桁数字として検証し、登録・更新時は各永続化方式の `BookDataValidator*` で一意性を確認します。
- `BookCreateRequest` / `BookResponse` / `BookSalesUnitPriceCreateRequest` には `salesUnitPrice` が含まれます。販売単価は `book_sales_unit_price_history` で履歴管理し、`BookUpdateRequest` では直接変更しません。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しないでください。
- 生成コードは直接編集せず、必要な場合だけ MyBatis Generator / Doma CodeGen / jOOQ CodeGen / OpenAPI Generator を実行してください。特に `src/main/java/com/example/demo/jooq/generated` は jOOQ 生成対象、`src/main/java/com/example/demo/openbd/generated` は OpenBD API クライアント生成対象です。

## 現在の API 仕様メモ

- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱います。`keyword` はタイトルまたは著者の前方一致条件として扱います。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とします。
- `page` は 0 始まりです。
- ページサイズは `application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込みます。
- `BookResponse` には `publisherId`、`publisherName`、`genreId`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` が含まれます。
- 販売単価履歴追加 API は `/api/books/{id}/sales-unit-prices` で、`BookSalesUnitPriceCreateRequest` を受け取り、成功時は空 body の 200 を返します。`effectiveFrom` は未来日として扱います。
- OpenBD 書誌取得 API は `/api/books/openbd` で、必須の `isbn` query parameter を受け取ります。`isbn` は 13 桁 ISBN またはカンマ区切りの 13 桁 ISBN として検証し、`OpenBdBookResponse` のリストを返します。
- 認証 API は `/api/auth/login` で、`LoginRequest` を受け取り、`LoginResponse` として `Bearer` token、ユーザー名、有効期限秒数を返します。
- ログイン回数制限のリセット API は `/api/auth/login-rate-limit/reset` で、Bearer token が必要です。
- 仕入登録 API は `/api/purchases/create` で、`PurchaseInvoiceCreateRequest` と明細リストを受け取り、`PurchaseInvoiceResponse` を返します。
- 仕入登録時は `supplierId`、`receivingStoreId`、明細の ISBN を参照チェックし、ISBN から本 ID を解決して明細金額と伝票金額を計算します。
- 外部キー参照先なし、ISBN 一意制約違反、販売単価履歴の一意制約違反、相関バリデーションエラー、データなし、OpenBD 書誌なし、OpenBD API 呼び出しエラー、更新競合、認証エラー、ログイン回数制限超過は `GlobalExceptionHandler` で ProblemDetail に変換されます。

## 負荷テスト方針

- 負荷テストは `docker/performance-tests` 配下の k6 スクリプトで行います。k6 は `docker/performance-tests/compose.yaml` で指定した `grafana/k6:2.2.0` の公式 Docker イメージを使用し、イメージはローカルに存在しない場合や `pull` した場合に取得されます。通常の `docker compose run` のたびに毎回再ダウンロードされるわけではありません。
- 既定の混合ワークロードは、書籍検索 70%、書籍取得 25%、仕入登録 5% です。OpenBD API は外部サービスなので負荷対象に含めません。
- JWT は k6 の `setup()` でテスト開始時に1回だけ取得し、仕入登録リクエストで再利用します。`/api/auth/login` を各リクエストで呼び出すと、日次ログイン回数制限と認証処理の負荷が測定に混入します。
- `docker/performance-tests/compose.yaml` の既定値は、ローカルで試しやすいように各負荷テストが約2分以内で終わる設定です。現在の段階負荷は `START_RPS=10`、`TARGET_RPS_STAGES=25,50,100`、`WARMUP_DURATION=10s`、`RAMP_DURATION=10s`、`STEP_DURATION=20s`、`RAMP_DOWN_DURATION=10s` を基本にします。
- 暫定的な合格基準は、HTTP 失敗率 1% 未満、check 成功率 99% 超、p95 レスポンスタイム 500ms 未満、p99 レスポンスタイム 1秒未満、`dropped_iterations` 0件です。これらは k6 の threshold としてコード化されています。
- 正式な持続可能最大性能を確認する場合は、候補 RPS を `HOLD_DURATION=10m` などで10分程度維持してください。長時間安定性を見る場合は `SOAK_DURATION=30m` または `SOAK_DURATION=60m` を使い、GC、メモリ増加、DB 接続枯渇、ロック失敗を確認してください。
- 仕入登録は `data.sql` に存在する在庫行のいずれかを更新します。テスト中は仕入伝票、明細、在庫増減履歴、在庫数量が蓄積・更新されるため、インメモリ H2 のデータとシーケンスを同じ初期状態へ戻すには測定ごとにアプリケーションを再起動してください。
- `purchase_invoice.id` などで H2 の主キー重複が出た場合は、テーブルの最大 ID とシーケンス再開値がずれていないか確認してください。`data.sql` の `ALTER SEQUENCE ... RESTART WITH` は、初期データ投入後の未使用の次の値に合わせる必要があります。
- 測定結果は、Spring Boot、選択した永続化実装、インメモリ H2 を組み合わせた性能として扱います。本番で別 DB を使う場合は、本番相当 DB で再測定してください。
- k6 の結果で `vus_max` は利用可能な最大 VU 数、`vus` は実際に使われた VU 数です。レスポンスが十分速い場合は、`PRE_ALLOCATED_VUS=100` でも実際の最大 VU が 1 のままになることがあります。`dropped_iterations` が 0 なら、少なくとも k6 側の VU 不足は発生していません。
- 結果比較では、アプリケーション JAR、Java オプション、profile、ホスト構成、初期データ、k6 イメージ、スクリプトパラメーター、ネットワーク配置を揃えてください。

## 脆弱性診断方針

- API 脆弱性診断は `docker/security-tests` 配下の OWASP ZAP 設定で行います。ZAP は `docker/security-tests/compose.yaml` で固定した `ghcr.io/zaproxy/zaproxy:2.17.0` の公式 Docker イメージを使用します。
- ローカル実行では、対象 API は Docker 内ではなくホスト側の Spring Boot JAR として起動します。ZAP だけを Docker コンテナで起動し、コンテナから `http://host.docker.internal:${APP_PORT}` 経由でホスト側 API を診断します。
- 実行はリポジトリルートから `docker/security-tests/run-zap.sh` を使います。スクリプトは `./gradlew bootJar`、診断用アプリ起動、`/v3/api-docs` の起動待ち、`/api/auth/login` による JWT 取得、認証必須 API の事前確認、ZAP 実行、レポート生成、起動したアプリプロセスの停止を行います。
- 既定の永続化 profile は `doma` です。`PERSISTENCE_PROFILE=jpa|mybatis|jooq` で切り替えられます。診断時は指定した永続化 profile と `dast` profile を組み合わせて起動します。
- 既定ポートは `APP_PORT=18080` です。指定ポートで既に別プロセスが待ち受けている場合、スクリプトは既存プロセスを診断せずに失敗します。既存プロセスを停止するか、`APP_PORT` で別ポートを指定してください。
- ZAP は `/v3/api-docs` から OpenAPI 定義を取り込み、`ZAP_AUTH_HEADER_VALUE` に設定した Bearer token を対象ホストへ付与して認証付き Active Scan を実行します。ログインはスクリプトが実行前に1回だけ行います。
- 診断対象はローカル API の `/api/**` に限定し、外部 OpenBD サービスを攻撃しないよう `/api/books/openbd` は `docker/security-tests/zap-api.yaml` で除外します。Swagger UI、Scalar、BootUI、H2 Console、Actuator 画面は OpenAPI ベースの API スキャン対象に含めません。
- Active Scan は攻撃 payload を送信し、データの登録・更新・削除が発生する可能性があります。診断先はスクリプトが起動する使い捨てのインメモリ H2 環境だけとし、共有環境、ステージング環境、本番環境には実行しないでください。
- レポートは `docker/security-tests/results` に HTML、JSON、SARIF として生成します。このディレクトリは `.gitignore` 対象です。
- Medium または High の alert が出た場合は終了コード `1` で失敗します。ただし、ローカル診断では HTTP で起動するため、`HTTP Only Site` だけは `docker/security-tests/zap-api.yaml` の alert filter で Info 扱いに下げています。それ以外の Medium / High alert は失敗扱いのままです。
- 初期導入では広い抑制リストを作らず、根拠のある誤検知だけを alert ID、対象 URL、理由を明確にして個別に alert filter へ追加してください。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

実装・修正タスクは、エラーが出ているテストを解消し、必要なテストが成功したことを確認してから完了としてください。`./gradlew test` が `UP-TO-DATE` で実テストを再実行しない場合は、必要に応じて `./gradlew test --rerun-tasks` を実行し、実際にテストが成功することを確認してください。

テストが失敗している状態、または実行できていない状態では実装完了として扱わず、失敗内容・未実行理由・残っている対応をユーザーへ明示してください。

永続化方式ごとの参照存在チェックや ISBN 一意性チェックを変更した場合は、`BookDataValidatorJPATest`、`BookDataValidatorMybatisTest`、`BookDataValidatorJooqTest` と、`PurchaseDataValidatorJPATest`、`PurchaseDataValidatorMybatisTest`、`PurchaseDataValidatorDomaTest`、`PurchaseDataValidatorJooqTest` を確認してください。

主キーシーケンス、採番処理、`data.sql` のシーケンス再設定、仕入登録の flush / 在庫ロック順序を変更した場合は、JPA / MyBatis / Doma / jOOQ の `BooksOperationService*Test` と `PurchaseOperationService*Test` を確認してください。

OpenBD API クライアント設定を変更した場合は `OpenBdClientConfigTest` を確認してください。OpenBD 書誌取得 API を変更した場合は `OpenBdBooksApiControllerTest` を確認してください。ページ計算を変更した場合は `PageCalculatorTest` を確認してください。

API、Security、DB 設定、JPA / MyBatis / Doma / jOOQ の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

BootUI の dependency や Security 設定を変更した場合は、`./gradlew bootRun` で起動し、`http://localhost:8080/bootui` が未認証で表示できることを確認してください。

`NativeRuntimeHints`、`application-native.yaml`、AOT / GraalVM 設定、`build.gradle` の通常テストと AOT テストの classpath 分離、ネイティブ実行時に利用する DTO・Doma Entity・リソースを変更した場合は、`./gradlew test --rerun-tasks` と `./gradlew processAot` を確認してください。テスト/AOT のタスク依存を変更した場合は `./gradlew nativeTest --dry-run` で `processTestAot` の経路が維持されていることも確認し、必要に応じて `./gradlew nativeTest`、`./gradlew nativeCompile`、生成された実行ファイルで動作確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- Java 21 で動作するコードを書いてください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
- コミットメッセージを提案・作成する場合は、先頭に `spring_java_orm　` を付けてください。
