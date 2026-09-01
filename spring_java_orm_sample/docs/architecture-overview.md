# アーキテクチャ概要

## 技術構成

このプロジェクトは Java 21 / Gradle / Spring Boot を使った書籍・仕入管理 API のサンプルです。

主な技術要素は Spring Web、Spring Validation、Spring Security、Spring Data JPA、MyBatis / MyBatis Generator、Doma / Doma CodeGen、jOOQ、H2、springdoc-openapi、ModelMapper、OpenAPI Generator、GraalVM Native Build Tools、開発時のみ利用する BootUI です。

現在の主な明示バージョンは、Spring Boot 4.1.1、GraalVM Native Build Tools 1.1.6、OpenAPI Generator 7.24.0、MyBatis Spring Boot Starter 4.1.0、Doma 3.14.0 / Doma Spring Boot Starter 3.0.0、springdoc-openapi 3.0.3、BootUI 1.13.1、H2 2.4.240、jackson-databind-nullable 0.2.11 です。Spring Boot BOM 管理下の依存は、原則として明示バージョンを追加せず BOM に従います。

## ドメインとデータ

主なドメインは `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement`、`book_sales_unit_price_history` です。

- `book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id` を参照します。
- `book_sales_unit_price_history.book_id` は `book.id` を参照します。
- `book.isbn` は13桁の一意な ISBN として扱います。
- 各テーブルの主キーはテーブル単位の `*_seq` シーケンスで採番します。
- 初期データは `src/main/resources/data.sql` で投入します。

書籍検索はページングされ、出版社名、ジャンル名、ISBN、現在販売単価、店舗別在庫を含む `BookPageResponse` を返します。現在販売単価は `book_sales_unit_price_history` の現在有効な履歴から取得します。

## API とアプリケーション層

- `AuthOperationApi`、`BooksOperationApi`、`OpenBdBooksApi`、`PurchaseOperationApi` が API 定義と OpenAPI 注釈を扱います。
- 各 `*ApiController` が対応する API を実装し、Service や外部クライアントに処理を委譲します。
- `AuthOperationApiController` は `LoginRateLimitService`、`AuthenticationManager`、`JwtTokenService` を使ってログイン回数制限、認証、Bearer token 発行、制限リセットを扱います。
- `OpenBdBooksApiController` は生成された `BooksApi` を呼び、ModelMapper で `OpenBdBookResponse` に変換します。
- `BooksOperationApiControllerValidator` が検索条件の相関バリデーションを扱います。
- `BooksOperationService` と `PurchaseOperationService` は4つの永続化方式で共有する Service インターフェースです。
- `PageCalculator` がページ数と offset、`ExceptionHandlerUtil` が validation error の組み立てを扱います。
- `SearchProperties` が `application.yaml` の検索ページサイズを読み込みます。
- `OpenBdClientConfig` が OpenAPI Generator 生成の `ApiClient`、`BooksApi`、`MetadataApi` を構成します。
- `NativeRuntimeHints` が DTO、Doma Entity、OpenBD 生成 DTO のリフレクション情報と Doma SQL、`generator-schema.sql` のリソースを AOT に登録します。

永続化方式ごとの構成と変換方針は `docs/persistence/` 配下を参照してください。

## profile

- デフォルト: `doma`
- JPA: `jpa`。Spring Data JPA repository と JPA auditing はこの profile でのみ有効です。
- MyBatis: `mybatis`
- jOOQ: `jooq`
- ネイティブ: `doma,native`
- 負荷試験: 選択した永続化 profile と `loadtest`
- 脆弱性診断: 選択した永続化 profile と `dast`

`native` profile では MyBatis、JPA、jOOQ の自動構成と H2 Console を無効化し、`generator-schema.sql` でスキーマを初期化します。

## 主なディレクトリ

- `src/main/java/com/example/demo/api`: API 定義、Controller、annotation、request / response DTO、validator、横断ログ
- `src/main/java/com/example/demo/api/annotation`: API 入力用の独自 Bean Validation annotation
- `src/main/java/com/example/demo/api/controller`: Controller
- `src/main/java/com/example/demo/api/log`: API 横断ログ
- `src/main/java/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/java/com/example/demo/data/domain`: 4方式で共有するドメイン型
- `src/main/java/com/example/demo/service`: 共通 Service インターフェース
- `src/main/java/com/example/demo/util`: ページ計算、例外処理補助などの共通ユーティリティ
- `src/main/java/com/example/demo/exception`: アプリケーション例外
- `src/main/java/com/example/demo/config`: Security、JWT、例外処理、検索、ロック再試行、runtime hints
- `src/main/java/com/example/demo/openbd`: OpenBD クライアント設定と生成コード
- `src/main/java/com/example/demo/openbd/config`: OpenBD クライアントの Spring 設定
- `src/main/java/com/example/demo/jpa`: JPA 実装
- `src/main/java/com/example/demo/jpa/config`: JPA profile 固有設定
- `src/main/java/com/example/demo/mybatis`: MyBatis の手書き実装と生成コード
- `src/main/java/com/example/demo/doma`: Doma の手書き実装と生成コード
- `src/main/java/com/example/demo/jooq`: jOOQ の手書き実装と生成コード
- `src/main/resources/application*.yaml`: 共通、JPA、loadtest、dast、native の Spring 設定
- `src/main/resources/generator-schema.sql`: MyBatis / Doma / jOOQ コード生成とネイティブ初期化に使うスキーマ
- `src/main/resources/data.sql`: 起動時の初期データとシーケンス再設定
- `src/main/resources/codegen`: Doma / jOOQ CodeGen 補助設定
- `src/main/resources/openapi/openbd_api_spec.yaml`: OpenBD クライアント生成用 OpenAPI 仕様
- `src/main/resources/com/example/demo/mybatis`: MyBatis の手書き・生成 Mapper XML
- `src/main/resources/META-INF/com/example/demo/doma`: Doma の手書き・生成 SQL
- `src/test/java/com/example/demo`: API、永続化、設定、validator、共通ユーティリティのテスト
- `src/test/java/com/example/demo/{jpa,mybatis,doma,jooq}/validator`: 永続化方式別 validator テスト
- `src/test/java/com/example/demo/openbd/config`: OpenBD クライアント設定テスト
- `src/test/java/com/example/demo/util`: 共通ユーティリティテスト
- `docker/performance-tests`: k6 負荷テスト
- `docker/security-tests`: OWASP ZAP 脆弱性診断

## 開発時の確認画面

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`
- Scalar: `http://localhost:8080/scalar`
- H2 Console: `http://localhost:8080/h2-console`
- BootUI: `http://localhost:8080/bootui`
- Actuator Health: `http://localhost:8080/actuator/health`

BootUI は `developmentOnly` dependency であり、`/bootui` と `/bootui/**` は Spring Security / JWT filter の対象外です。Actuator の Web 公開は `/actuator/health` のみに限定します。
