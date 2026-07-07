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

API は `/api/auth`、`/api/books`、`/api/purchases` 配下にあり、H2 のインメモリデータベースを使用します。初期データは `src/main/resources/data.sql` で投入されます。

現在の主なドメインは `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_invoice`、`purchase_invoice_detail`、`book_stock`、`book_stock_movement` です。`book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id` を参照します。`book.isbn` は 13 桁の一意な ISBN として扱います。検索 API はページングされ、出版社名・ジャンル名・ISBN・在庫リストを含む `BookPageResponse` を返します。

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

アプリ起動後に確認できる画面:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html#/`
- Scalar: `http://localhost:8080/scalar`
- H2 Console: `http://localhost:8080/h2-console`

生成物を更新する意図がある場合のみ、以下を実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
```

`compileJava` は `generateJooq` に依存しているため、通常のビルド時にも jOOQ 生成コードが更新される可能性があります。

## ディレクトリ構成

- `src/main/java/com/example/demo/api`: API インターフェース
- `src/main/java/com/example/demo/api/controller`: Controller
- `src/main/java/com/example/demo/api/log`: API 横断ログ
- `src/main/java/com/example/demo/api/request`: request DTO
- `src/main/java/com/example/demo/api/response`: response DTO
- `src/main/java/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/java/com/example/demo/data/domain`: JPA / MyBatis / Doma / jOOQ で共有するドメイン型
- `src/main/java/com/example/demo/service`: アプリケーション共通の Service インターフェース、ページ計算
- `src/main/java/com/example/demo/exception`: アプリケーション例外
- `src/main/java/com/example/demo/config`: Spring 設定、Security / JWT、ログイン回数制限、例外ハンドリング、検索設定、ロック失敗リトライ設定
- `src/main/java/com/example/demo/jpa`: JPA 実装。Entity、Repository、Service、converter、型変換、データバリデーションを含みます。
- `src/main/java/com/example/demo/mybatis`: MyBatis 実装。手書き Mapper / 表示向け Entity / Service / converter / TypeHandler / データバリデーションと、Generator 生成コードを含みます。
- `src/main/java/com/example/demo/doma`: Doma 実装。手書き DAO / 表示向け Entity / AggregateStrategy / Service / converter / データバリデーションと、CodeGen 生成コードを含みます。
- `src/main/java/com/example/demo/jooq`: jOOQ 実装。Service、DSL component、converter、validator、表示向け row、jOOQ 生成コードを含みます。
- `src/main/java/com/example/demo/jooq/dsl`: jOOQ の手書き SQL / DSL 組み立て
- `src/main/java/com/example/demo/jooq/generated`: jOOQ 生成コード
- `src/main/resources/application.yaml`: アプリケーション設定
- `src/main/resources/application-jpa.yaml`: JPA profile 用の Spring Data JPA Repository 有効化設定
- `src/main/resources/mybatis-config.xml`: MyBatis TypeHandler 設定
- `src/main/resources/codegen`: Doma CodeGen / jOOQ CodeGen 補助設定
- `src/main/resources/com/example/demo/mybatis/mapper`: 手書き MyBatis SQL
- `src/main/resources/com/example/demo/mybatis/generator/mapper`: MyBatis Generator 生成 SQL
- `src/main/resources/META-INF/com/example/demo/doma/dao`: 手書き Doma SQL
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao`: Doma CodeGen 生成 SQL
- `src/main/resources/data.sql`: 起動時の初期データ
- `src/main/resources/generator-schema.sql`: MyBatis Generator / Doma CodeGen / jOOQ CodeGen 用スキーマ
- `src/test/java/com/example/demo`: アプリケーション、API、永続化実装、例外ハンドリングのテスト

## 重要な設計方針

- `BooksOperationApi` は API 定義と OpenAPI 注釈を扱います。
- `PurchaseOperationApi` は仕入 API 定義と OpenAPI 注釈を扱います。
- `AuthOperationApi` は認証 API 定義と OpenAPI 注釈を扱います。
- `BooksOperationApiController` は `BooksOperationApi` を実装し、Service に処理を委譲します。
- `PurchaseOperationApiController` は `PurchaseOperationApi` を実装し、Service に処理を委譲します。
- `AuthOperationApiController` は `AuthOperationApi` を実装し、`LoginRateLimitService`、`AuthenticationManager`、`JwtTokenService` でログイン回数制限、認証、Bearer token 発行、ログイン回数制限リセットを扱います。
- `BooksOperationApiControllerValidator` は API 入力の相関バリデーションを扱います。
- `BooksOperationService` は JPA / MyBatis / Doma / jOOQ 共通の Service インターフェースです。
- `PurchaseOperationService` は JPA / MyBatis / Doma / jOOQ 共通の仕入登録 Service インターフェースです。
- `PageCalculator` はページ数と offset の計算を扱います。
- `SearchProperties` は検索 API のページサイズ設定を扱います。
- `BookOperationConverterJPA` / `BookOperationConverterMybatis` / `BookOperationConverterDoma` / `BookOperationConverterJooq` は本情報と `book_stock` / `store` 由来の在庫表示情報を `BookResponse` / `BookStockResponse` に変換します。
- JPA の取得・検索は `BookRepository.BookWithStockRowProjection` の在庫行を `BookOperationConverterJPA` で書籍単位に集約します。
- MyBatis の取得・検索は `BookWithPublisherName` と `BookStockWithStoreName` を `BookCustomMapper.xml` の nested collection で組み立てます。
- Doma の取得・検索は `BookWithPublisherNameAggregateStrategy` で `bookStockList` を集約します。
- jOOQ の取得・検索は `BookWithStockRow` の在庫行を `BookOperationConverterJooq` で書籍単位に集約します。
- jOOQ の手書き SQL / DSL 組み立ては `BookOperationDsl` / `PurchaseOperationDsl` に集約します。参照存在チェックは `BookDsl` / `BookGenreDsl` / `PublisherDsl` / `StoreDsl` / `SupplierDsl` を使います。
- 仕入登録は JPA / MyBatis / Doma / jOOQ の各 `PurchaseOperationService*` が `PurchaseInvoice` / `PurchaseInvoiceDetail` 相当のデータを登録し、在庫をロックして新規作成または数量加算し、`book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` の在庫増減履歴を登録します。
- `PurchaseInvoiceType` は仕入伝票種別を表す共有ドメイン型です。JPA は `PurchaseInvoiceTypeConverter`、MyBatis は `PurchaseInvoiceTypeHandler`、Doma は `@Domain`、jOOQ は converter / Service 側の値変換で扱います。
- `BookStockMovementType` と `BookStockMovementSourceType` は在庫増減履歴の共有ドメイン型です。JPA は converter、MyBatis は TypeHandler、Doma は `@Domain`、jOOQ は Service / DSL 側の値変換で扱います。
- MyBatis Generator の `purchase_invoice` / `purchase_invoice_detail` は、現在 `PurchaseOrderEntity` / `PurchaseOrderDetailEntity`、`PurchaseOrderMapper` / `PurchaseOrderDetailMapper` という生成名です。`book_stock` は `BookStockEntity` / `BookStockMapper`、`book_stock_movement` は `BookStockMovementEntity` / `BookStockMovementMapper` として生成されます。生成名を変更する場合は影響範囲を確認してください。
- 現在のデフォルト profile は `application.yaml` の `spring.profiles.default: doma` です。通常起動では `BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` が使われます。
- 認証設定は `application.yaml` の `app.auth` 配下で管理します。`app.auth.login-rate-limit` はログインの日次回数制限を扱います。`/api/auth/login` は公開され、書籍の取得・検索以外の API は Bearer token が必要です。
- API の入出力には Entity ではなく request / response DTO を使ってください。
- `BookCreateRequest` / `BookUpdateRequest` / `BookResponse` には `isbn` が含まれます。ISBN は `@Isbn` で 13 桁数字として検証し、登録・更新時は各永続化方式の `BookDataValidator*` で一意性を確認します。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しないでください。
- 生成コードは直接編集せず、必要な場合だけ MyBatis Generator / Doma CodeGen / jOOQ CodeGen を実行してください。特に `src/main/java/com/example/demo/jooq/generated` は jOOQ 生成対象です。

## 現在の API 仕様メモ

- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱います。`keyword` はタイトルまたは著者の前方一致条件として扱います。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とします。
- `page` は 0 始まりです。
- ページサイズは `application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込みます。
- `BookResponse` には `publisherId`、`publisherName`、`genreId`、`genreName`、`isbn`、`bookStockList` が含まれます。
- 認証 API は `/api/auth/login` で、`LoginRequest` を受け取り、`LoginResponse` として `Bearer` token、ユーザー名、有効期限秒数を返します。
- ログイン回数制限のリセット API は `/api/auth/login-rate-limit/reset` で、Bearer token が必要です。
- 仕入登録 API は `/api/purchases/create` で、`PurchaseInvoiceCreateRequest` と明細リストを受け取り、`PurchaseInvoiceResponse` を返します。
- 仕入登録時は `supplierId`、`receivingStoreId`、明細の ISBN を参照チェックし、ISBN から本 ID を解決して明細金額と伝票金額を計算します。
- 外部キー参照先なし、ISBN 一意制約違反、相関バリデーションエラー、データなし、更新競合、認証エラー、ログイン回数制限超過は `GlobalExceptionHandler` で ProblemDetail に変換されます。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

実装・修正タスクは、エラーが出ているテストを解消し、必要なテストが成功したことを確認してから完了としてください。`./gradlew test` が `UP-TO-DATE` で実テストを再実行しない場合は、必要に応じて `./gradlew test --rerun-tasks` を実行し、実際にテストが成功することを確認してください。

テストが失敗している状態、または実行できていない状態では実装完了として扱わず、失敗内容・未実行理由・残っている対応をユーザーへ明示してください。

API、Security、DB 設定、JPA / MyBatis / Doma / jOOQ の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- `readme.md` には API の curl 例があります。エンドポイント仕様を変更した場合のみ更新してください。
- Java 21 で動作するコードを書いてください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
- コミットメッセージを提案・作成する場合は、先頭に `spring_java_orm　` を付けてください。
