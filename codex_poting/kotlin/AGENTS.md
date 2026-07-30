# AGENTS.md

## プロジェクト概要

このプロジェクトは、Java 版 `spring_java_orm_sample` をもとにした Kotlin / Java 21 / Gradle / Spring Boot の書籍・仕入管理 API サンプルです。

Kotlin 移植版では JPA を除外し、永続化方式は MyBatis / Doma / jOOQ を残しています。手書きソースは Kotlin 化済みで、`src/main/kotlin` 配下に残る Java ファイルは MyBatis Generator / Doma CodeGen / jOOQ CodeGen / OpenAPI Generator の生成コードのみです。

主な技術要素:

- Kotlin JVM / Kotlin Spring plugin / kapt
- Spring Web
- Spring Security
- Spring Validation
- MyBatis / MyBatis Generator
- Doma / Doma CodeGen
- jOOQ
- H2 Database
- springdoc-openapi
- ModelMapper
- OpenAPI Generator

API は `/api/auth`、`/api/books`、`/api/purchases` 配下にあります。H2 のインメモリ DB を使用し、`schema.sql` でスキーマ、`data.sql` で初期データを投入します。`/api/books/openbd` では OpenBD API クライアントを使って外部書誌情報を取得します。

## 追加の作業規約

Codex は作業内容に応じて以下も確認してください。

- 実装・修正・レビュー全般: `docs/codex-implementation-rules.md`
- MyBatis / Doma / jOOQ / DB スキーマ / 生成コードに関わる変更: `docs/persistence-implementation-notes.md`

補足ドキュメントには元 Java/JPA サンプル由来の記述が残っている場合があります。この Kotlin 版では、JPA 実装・JPA profile・JPA 依存・JPA テストは存在しないものとして扱ってください。

## よく使うコマンド

Gradle Wrapper を使用してください。

```shell
./gradlew test
./gradlew bootRun
./gradlew build
```

アプリ起動後に確認できる画面:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`
- Scalar: `http://localhost:8080/scalar`
- H2 Console: `http://localhost:8080/h2-console`

生成物を更新する意図がある場合のみ、以下を実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
./gradlew syncOpenBdGeneratedSources
```

`compileKotlin` と `kaptGenerateStubsKotlin` は `generateJooq` と `syncOpenBdGeneratedSources` に依存しています。通常のビルド時にも jOOQ 生成コードや OpenBD 生成コードが更新される可能性があります。

## ディレクトリ構成

- `src/main/kotlin/com/example/demo/api`: API インターフェース
- `src/main/kotlin/com/example/demo/api/annotation`: API 入力用の独自 Bean Validation annotation
- `src/main/kotlin/com/example/demo/api/controller`: Controller
- `src/main/kotlin/com/example/demo/api/log`: API 横断ログ
- `src/main/kotlin/com/example/demo/api/request`: request DTO
- `src/main/kotlin/com/example/demo/api/response`: response DTO
- `src/main/kotlin/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/kotlin/com/example/demo/data/domain`: MyBatis / Doma / jOOQ で共有するドメイン型
- `src/main/kotlin/com/example/demo/service`: アプリケーション共通の Service インターフェース
- `src/main/kotlin/com/example/demo/util`: 共通ユーティリティ。ページ計算、例外ハンドリング補助を含みます。
- `src/main/kotlin/com/example/demo/exception`: アプリケーション例外
- `src/main/kotlin/com/example/demo/config`: Spring 設定、Security / JWT、ログイン回数制限、例外ハンドリング、検索設定、ロック失敗リトライ設定
- `src/main/kotlin/com/example/demo/openbd`: OpenBD API クライアント設定と OpenAPI Generator 生成コード
- `src/main/kotlin/com/example/demo/mybatis`: MyBatis 実装。手書き Mapper / 表示向け Entity / Service / converter / TypeHandler / validator / Generator 生成コードを含みます。
- `src/main/kotlin/com/example/demo/doma`: Doma 実装。手書き DAO / 表示向け Entity / AggregateStrategy / Service / converter / validator / CodeGen 生成コードを含みます。
- `src/main/kotlin/com/example/demo/jooq`: jOOQ 実装。Service、DSL component、converter、validator、表示向け row、jOOQ 生成コードを含みます。
- `src/main/resources/application.yaml`: アプリケーション設定。default profile は `doma` です。
- `src/main/resources/schema.sql`: Spring Boot SQL init 用スキーマ。
- `src/main/resources/generator-schema.sql`: MyBatis Generator / Doma CodeGen / jOOQ CodeGen 用スキーマ。
- `src/main/resources/data.sql`: 起動時の初期データ。
- `src/test/kotlin/com/example/demo`: アプリケーション、API、MyBatis / Doma / jOOQ、例外ハンドリングのテスト。

## 重要な設計方針

- JPA はこの Kotlin 版の対象外です。`spring-boot-starter-data-jpa`、`application-jpa.yaml`、`com.example.demo.jpa`、JPA 専用テストを追加しないでください。
- Lombok は使用しません。Kotlin の `data class`、property、primary constructor、または通常の Java 実装で表現してください。
- `BooksOperationApi` / `PurchaseOperationApi` / `AuthOperationApi` / `OpenBdBooksApi` は API 定義と OpenAPI 注釈を扱います。
- OpenAPI の `examples` は API interface の `@ApiResponse` / request body annotation に保持します。Kotlin では `ExampleObject` を import し、単数でも `examples = [ExampleObject(...)]` の形で記述してください。
- Controller は API interface を実装し、Service に処理を委譲します。
- `BooksOperationService` と `PurchaseOperationService` は MyBatis / Doma / jOOQ 共通の Service インターフェースです。
- 現在の default profile は `application.yaml` の `spring.profiles.default: doma` です。通常起動では `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` が使われます。
- MyBatis の取得・検索は `BookWithPublisherName` と `BookStockWithStoreName` を `BookCustomMapper.xml` の nested collection で組み立てます。
- Doma の取得・検索は `BookWithPublisherNameAggregateStrategy` で `bookStockList` を集約します。
- jOOQ の取得・検索は `BookWithStockRow` の在庫行を `BookOperationConverterJooq` で書籍単位に集約します。
- 本の登録時は `BookCreateRequest.salesUnitPrice` から `book_sales_unit_price_history` の初期履歴を作成します。販売単価履歴追加 API は前後履歴の `effective_to` を調整します。
- 仕入登録は `PurchaseOperationServiceMybatis` / `PurchaseOperationServiceDoma` / `PurchaseOperationServiceJooq` が仕入伝票と明細を登録し、在庫をロックして新規作成または数量加算し、`book_stock_movement` に履歴を登録します。
- `PurchaseInvoiceType`、`BookStockMovementType`、`BookStockMovementSourceType` は共有ドメイン型です。MyBatis は TypeHandler、Doma は `@Domain`、jOOQ は converter / Service 側の値変換で扱います。
- API の入出力には Entity ではなく request / response DTO を使ってください。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しないでください。
- 生成コードは直接編集せず、必要な場合だけ MyBatis Generator / Doma CodeGen / jOOQ CodeGen / OpenAPI Generator を実行してください。
- 手書き Java を追加しないでください。新規・修正する手書きコードは Kotlin で実装し、生成コードだけ Java のまま許容します。

## 現在の API 仕様メモ

- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱います。
- `keyword` はタイトルまたは著者の前方一致条件として扱い、大文字小文字を無視します。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とします。
- `page` は 0 始まりです。
- ページサイズは `application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込みます。
- `BookResponse` には `publisherId`、`publisherName`、`genreId`、`genreName`、`isbn`、`salesUnitPrice`、`bookStockList` が含まれます。
- 販売単価履歴追加 API は `/api/books/{id}/sales-unit-prices` で、成功時は空 body の 200 を返します。
- OpenBD 書誌取得 API は `/api/books/openbd` で、必須の `isbn` query parameter を受け取ります。
- 認証 API は `/api/auth/login` で、`Bearer` token、ユーザー名、有効期限秒数を返します。
- ログイン回数制限のリセット API は `/api/auth/login-rate-limit/reset` で、Bearer token が必要です。
- 仕入登録 API は `/api/purchases/create` で、`PurchaseInvoiceCreateRequest` と明細リストを受け取り、`PurchaseInvoiceResponse` を返します。
- 外部キー参照先なし、ISBN 一意制約違反、相関バリデーションエラー、データなし、OpenBD 書誌なし、外部 API 呼び出しエラー、更新競合、認証エラー、ログイン回数制限超過は `GlobalExceptionHandler` で ProblemDetail に変換されます。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

実装・修正タスクは、エラーが出ているテストを解消し、必要なテストが成功したことを確認してから完了としてください。`./gradlew test` が `UP-TO-DATE` で実テストを再実行しない場合は、必要に応じて `./gradlew test --rerun-tasks` を実行してください。

永続化方式ごとの参照存在チェックや ISBN 一意性チェックを変更した場合は、MyBatis / Doma / jOOQ の service / validator test を確認してください。

OpenBD API クライアント設定を変更した場合は `OpenBdClientConfigTest` を確認してください。OpenBD 書誌取得 API を変更した場合は `OpenBdBooksApiControllerTest` を確認してください。ページ計算を変更した場合は `PageCalculatorTest` を確認してください。

API、Security、DB 設定、MyBatis / Doma / jOOQ の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- Kotlin / Java 21 で動作するコードを書いてください。
- Kotlin ファイルは `.editorconfig` に従い、4 spaces、LF、UTF-8 を使ってください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
- コミットメッセージを提案・作成する場合は、先頭に `kotlin_orm　` を付けてください。
