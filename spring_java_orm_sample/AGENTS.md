# AGENTS.md

## プロジェクト概要

このプロジェクトは、Java 21 / Gradle / Spring Boot を使った書籍管理 API のサンプルアプリケーションです。

主な技術要素は以下です。

- Spring Web
- Spring Data JPA
- MyBatis
- MyBatis Generator
- Doma
- Doma CodeGen
- H2 Database
- Spring Validation
- Spring Security
- springdoc-openapi

API は `/api/books` 配下にあり、H2 のインメモリデータベースを使用します。初期データは `src/main/resources/data.sql` で投入されます。

現在の主なドメインは `book`、`publisher`、`book_genre`、`supplier`、`store`、`purchase_order`、`purchase_order_detail`、`book_stock` です。`book.publisher_id` は `publisher.id`、`book.genre_id` は `book_genre.id` を参照します。検索 API はページングされ、出版社名・ジャンル名・在庫リストを含む `BookPageResponse` を返します。

## 追加の作業規約

Codex は作業内容に応じて以下も確認してください。

- 実装・修正・レビュー全般: `docs/codex-implementation-rules.md`
- JPA / MyBatis / Doma / DB スキーマ / 生成コードに関わる変更: `docs/persistence-implementation-notes.md`

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
```

## ディレクトリ構成

- `src/main/java/com/example/demo/api`: API インターフェース、API 横断処理
- `src/main/java/com/example/demo/api/controller`: Controller
- `src/main/java/com/example/demo/api/request`: request DTO
- `src/main/java/com/example/demo/api/response`: response DTO
- `src/main/java/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/java/com/example/demo/data/domain`: JPA / MyBatis / Doma で共有するドメイン型
- `src/main/java/com/example/demo/service`: アプリケーション共通の Service インターフェース、ページ計算
- `src/main/java/com/example/demo/exception`: アプリケーション例外
- `src/main/java/com/example/demo/converter`: projection / 表示向け Entity から response DTO への変換、在庫行の集約
- `src/main/java/com/example/demo/config`: Spring 設定、例外ハンドリング、検索設定、ロック失敗リトライ設定
- `src/main/java/com/example/demo/jpa`: JPA 実装。Entity、Repository、Service、型変換、データバリデーションを含みます。
- `src/main/java/com/example/demo/mybatis`: MyBatis 実装。手書き Mapper / 表示向け Entity / Service / TypeHandler / データバリデーションと、Generator 生成コードを含みます。
- `src/main/java/com/example/demo/doma`: Doma 実装。手書き DAO / 表示向け Entity / AggregateStrategy / Service / データバリデーションと、CodeGen 生成コードを含みます。
- `src/main/resources/application.yaml`: アプリケーション設定
- `src/main/resources/mybatis-config.xml`: MyBatis TypeHandler 設定
- `src/main/resources/codegen`: Doma CodeGen 補助設定
- `src/main/resources/com/example/demo/mybatis/mapper`: 手書き MyBatis SQL
- `src/main/resources/com/example/demo/mybatis/generator/mapper`: MyBatis Generator 生成 SQL
- `src/main/resources/META-INF/com/example/demo/doma/dao`: 手書き Doma SQL
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao`: Doma CodeGen 生成 SQL
- `src/main/resources/data.sql`: 起動時の初期データ
- `src/main/resources/generator-schema.sql`: MyBatis Generator / Doma CodeGen 用スキーマ
- `src/test/java/com/example/demo`: アプリケーション、API、永続化実装、例外ハンドリングのテスト

## 重要な設計方針

- `BooksOperationApi` は API 定義と OpenAPI 注釈を扱います。
- `BooksOperationApiController` は `BooksOperationApi` を実装し、Service に処理を委譲します。
- `BooksOperationApiControllerValidator` は API 入力の相関バリデーションを扱います。
- `BooksOperationService` は JPA / MyBatis / Doma 共通の Service インターフェースです。
- `PageCalculator` はページ数と offset の計算を扱います。
- `SearchProperties` は検索 API のページサイズ設定を扱います。
- `BookConverter` は本情報と `book_stock` / `store` 由来の在庫表示情報を `BookResponse` / `BookStockResponse` に変換します。
- JPA の取得・検索は `BookRepository.BookWithStockRowProjection` の在庫行を `BookConverter` で書籍単位に集約します。
- MyBatis の取得・検索は `BookWithPublisherName` と `BookStockWithStoreName` を `BookCustomMapper.xml` の nested collection で組み立てます。
- Doma の取得・検索は `BookWithPublisherNameAggregateStrategy` で `bookStockList` を集約します。
- `PurchaseOrderType` は仕入伝票種別を表す共有ドメイン型です。JPA は `PurchaseOrderTypeConverter`、MyBatis は `PurchaseOrderTypeHandler`、Doma は `@Domain` で扱います。
- 現在のデフォルト実装は `BooksOperationServiceDoma` です。
- API の入出力には Entity ではなく request / response DTO を使ってください。
- 更新・削除処理では、既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しないでください。
- 生成コードは直接編集せず、必要な場合だけ Generator / CodeGen を実行してください。

## 現在の API 仕様メモ

- 検索 API は任意の `keyword`、任意の `releaseDateFrom` / `releaseDateTo`、必須の `page` を扱います。`keyword` はタイトルまたは著者の前方一致条件として扱います。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とします。
- `page` は 0 始まりです。
- ページサイズは `application.yaml` の `search.page-size` で定義し、`SearchProperties` で読み込みます。
- `BookResponse` には `publisherId`、`publisherName`、`genreId`、`genreName`、`bookStockList` が含まれます。
- 外部キー参照先なし、相関バリデーションエラー、データなし、更新競合は `GlobalExceptionHandler` で ProblemDetail に変換されます。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

API、Security、DB 設定、JPA / MyBatis / Doma の実装切り替えを変更した場合は、必要に応じて `./gradlew bootRun` で起動確認し、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- `readme.md` には API の curl 例があります。エンドポイント仕様を変更した場合のみ更新してください。
- Java 21 で動作するコードを書いてください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
