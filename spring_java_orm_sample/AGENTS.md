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

API は `/api/books` 配下にあり、H2 のインメモリデータベースを使用します。初期データは `src/main/resources/data.sql` で投入されます。現在のドメインは `book` と `publisher` で、`book.publisher_id` は `publisher.id` を参照します。

## 追加の作業規約

この `AGENTS.md` に加えて、Codex は実装・修正・レビュー作業を開始する前に必ず以下の追加規約を確認してください。

- `docs/codex-implementation-rules.md`

`docs/codex-implementation-rules.md` の内容は、このプロジェクトで Codex がコード変更を行う際の補足ルールです。`AGENTS.md` と矛盾する場合は、より具体的な指示を優先してください。

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

MyBatis Generator は、生成物を更新する意図がある場合のみ実行してください。

```shell
./gradlew runMyBatisGenerator
```

Doma CodeGen は、Doma の生成物を更新する意図がある場合のみ実行してください。

```shell
./gradlew domaCodeGenLocalAll
```

## ディレクトリ構成

- `src/main/java/com/example/demo/api`: API インターフェース、API 横断処理
- `src/main/java/com/example/demo/api/controller`: REST Controller
- `src/main/java/com/example/demo/api/request`: リクエスト DTO
- `src/main/java/com/example/demo/api/response`: レスポンス DTO
- `src/main/java/com/example/demo/api/validator`: API 入力の相関バリデーション
- `src/main/java/com/example/demo/service`: アプリケーション共通の Service インターフェース
- `src/main/java/com/example/demo/exception`: アプリケーション例外
- `src/main/java/com/example/demo/converter`: Entity / DTO 変換
- `src/main/java/com/example/demo/config`: Spring 設定、例外ハンドリング
- `src/main/java/com/example/demo/jpa`: JPA 実装
- `src/main/java/com/example/demo/jpa/entity`: JPA Entity
- `src/main/java/com/example/demo/jpa/repository`: Spring Data JPA Repository
- `src/main/java/com/example/demo/jpa/service`: JPA 版 Service 実装
- `src/main/java/com/example/demo/jpa/validator`: JPA 版データバリデーション
- `src/main/java/com/example/demo/mybatis`: MyBatis 実装
- `src/main/java/com/example/demo/mybatis/service`: MyBatis 版 Service 実装
- `src/main/java/com/example/demo/mybatis/mapper`: 手書きの MyBatis Mapper
- `src/main/java/com/example/demo/mybatis/validator`: MyBatis 版データバリデーション
- `src/main/java/com/example/demo/mybatis/generator`: MyBatis Generator の生成コード
- `src/main/resources/com/example/demo/mybatis/mapper`: 手書き MyBatis Mapper XML
- `src/main/resources/com/example/demo/mybatis/generator`: MyBatis Generator の生成 Mapper XML
- `src/main/java/com/example/demo/doma`: Doma 実装
- `src/main/java/com/example/demo/doma/service`: Doma 版 Service 実装
- `src/main/java/com/example/demo/doma/dao`: 手書きの Doma DAO
- `src/main/java/com/example/demo/doma/validator`: Doma 版データバリデーション
- `src/main/java/com/example/demo/doma/generator`: Doma CodeGen の生成コード
- `src/main/resources/META-INF/com/example/demo/doma/dao`: 手書き Doma DAO 用 SQL
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao`: Doma CodeGen の生成 DAO 用 SQL
- `src/main/resources/application.yaml`: アプリケーション設定
- `src/main/resources/data.sql`: 起動時の初期データ
- `src/main/resources/generator-schema.sql`: MyBatis Generator / Doma CodeGen 用スキーマ
- `src/test/java/com/example/demo/api/validator`: API バリデーションのテスト
- `src/test/java/com/example/demo/jpa/service`: JPA 版 Service のテスト
- `src/test/java/com/example/demo/mybatis/service`: MyBatis 版 Service のテスト
- `src/test/java/com/example/demo/doma/service`: Doma 版 Service のテスト
- `src/test/java/com/example/demo/doma/generator/dao`: Doma 生成 DAO のテスト

## 開発方針

- 既存のレイヤー構造を尊重してください。
  - `BookApi` は API 定義と OpenAPI 注釈を扱う
  - `BooksApiController` は `BookApi` を実装し、Service に処理を委譲する
  - `BookApiControllerValidator` は API 入力の相関バリデーションを扱う
  - `BookService` は JPA / MyBatis / Doma 共通の Service インターフェース
  - `BookDataValidatorJPA` / `BookDataValidatorMybatis` / `BookDataValidatorDoma` は永続化方式ごとのデータバリデーションを扱う
  - `jpa` パッケージは JPA 実装
  - `mybatis` パッケージは MyBatis 実装
  - `doma` パッケージは Doma 実装
  - `converter` は Entity / DTO 変換を扱う
- 現在のデフォルト実装は `BookServiceDoma` です。`@Primary` を変更する場合は、JPA / MyBatis / Doma のどれを使うのかを明確にしてください。
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングは避けてください。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にしてください。
- API の入出力には Entity ではなく request / response DTO を使ってください。
- DB を読む・更新する Service メソッドには `@Transactional` を付けてください。
- 検索 API は `title` と任意の `releaseDateFrom` / `releaseDateTo` を扱います。日付範囲の相関チェックは `BookApiControllerValidator` に集約してください。
- `releaseDateFrom` / `releaseDateTo` は両方指定、または両方未指定を基本とし、片方だけの指定や From > To は相関バリデーションエラーとして扱います。
- `publisherId` は `publisher` への外部キーです。登録・更新時の参照存在チェックは各永続化方式の `BookDataValidator*` に集約してください。
- 更新・削除処理では、既存のバージョンチェックと書き込みロックを不用意に変更しないでください。
- 更新競合は `ObjectOptimisticLockingFailureException` と `GlobalExceptionHandler` により HTTP 409 として扱われます。
- データなしは `RepositoryDataNotfoundException` と `GlobalExceptionHandler` により HTTP 404 として扱われます。
- 相関バリデーションエラーは `CorrelationValidationFailureException` と `GlobalExceptionHandler` により HTTP 400 として扱われます。
- 外部キー参照先なしは `ForeignKeyReferenceNotFoundException` と `GlobalExceptionHandler` により HTTP 400 として扱われます。

## API に関する注意

- API 仕様を変更する場合は、`BookApi`、`BooksApiController`、request / response DTO、`BookApiControllerValidator`、`GlobalExceptionHandler`、`readme.md` の整合性を確認してください。
- OpenAPI 注釈は `BookApi` に集約されています。Controller 側へ重複して追加しないでください。
- `BookCreateRequest`、`BookUpdateRequest`、`BookResponse` には `releaseDate` と `publisherId` が含まれます。スキーマや永続化層を変更する場合は DTO も確認してください。

## Doma に関する注意

- `src/main/java/com/example/demo/doma/generator` 配下は Doma CodeGen の生成コードです。手作業での編集は避けてください。
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao` 配下の SQL も生成物として扱ってください。
- 手書き SQL は `BookCustomDao` と `src/main/resources/META-INF/com/example/demo/doma/dao` 配下に追加してください。
- Doma の DAO メソッドを追加する場合は、対応する SQL ファイルのパスとメソッド名を揃えてください。
- `BookDao` / `PublisherDao` は Doma CodeGen の生成 DAO です。生成元スキーマとの整合性を維持してください。
- Doma CodeGen の対象スキーマを変える場合は、`generator-schema.sql` と `build.gradle` の `domaCodeGen` 設定の整合性を確認してください。
- Doma 側の更新・削除では、Doma の楽観ロック例外を `ObjectOptimisticLockingFailureException` に変換する既存方針を維持してください。

## MyBatis に関する注意

- `src/main/java/com/example/demo/mybatis/generator` 配下は MyBatis Generator の生成コードです。手作業での編集は避けてください。
- `src/main/resources/com/example/demo/mybatis/generator` 配下の XML も生成物として扱ってください。
- 手書き SQL は `BookCustomMapper` と `BookCustomMapper.xml` に追加してください。
- `BookMapper` / `PublisherMapper` は MyBatis Generator の生成 Mapper です。生成元スキーマとの整合性を維持してください。
- MyBatis Generator の対象スキーマを変える場合は、`generator-schema.sql` と `generatorConfig.xml` の整合性を確認してください。
- `application.yaml` の `mybatis.mapper-locations` は、MyBatis の XML 読み込みに必要です。不用意に変更しないでください。

## JPA に関する注意

- JPA 実装は `BookServiceJPA`、`BookRepository`、`PublisherRepository`、`Book`、`Publisher` を中心に構成されています。
- JPA 側の検索は Spring Data JPA Repository メソッドまたは明示的な `@Query` を優先してください。
- JPA 側の更新・削除では `findByIdWithWriteLock` による書き込みロックを維持してください。
- JPA 側の `publisherId` 参照存在チェックは `PublisherRepository` を使う `BookDataValidatorJPA` に集約してください。

## スキーマ変更時の注意

- `book` または `publisher` テーブルのカラムを変更する場合は、以下の整合性を確認してください。
  - JPA Entity
  - JPA Repository
  - MyBatis Generator の生成 Entity / Mapper
  - Doma CodeGen の生成 Entity / DAO / SQL
  - 手書き MyBatis Mapper XML
  - 手書き Doma SQL
  - 各永続化方式の `BookDataValidator*`
  - request / response DTO
  - `BookConverter`
  - `data.sql`
  - `generator-schema.sql`
  - `generatorConfig.xml`
- `generator-schema.sql` は MyBatis Generator と Doma CodeGen の両方で使われます。片方だけを想定した変更にしないでください。
- `release_date` は検索条件と DTO に関係します。変更時は API バリデーションと3つの Service 実装を確認してください。
- `publisher_id` は `publisher` への外部キーです。変更時は初期データ、生成 Mapper/DAO、外部キー参照チェックを確認してください。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

対象が限定されている場合でも、関連するテストを確認してください。

- API 相関バリデーション: `BookApiControllerValidatorTest`
- JPA 実装: `BookServiceJPATest`
- MyBatis 実装: `BookServiceMybatisTest`
- Doma 実装: `BookServiceDomaTest`
- Doma 生成 DAO: `BookDaoTest`
- Doma 生成 Publisher DAO: `PublisherDaoTest`

API、Security、DB 設定、JPA / MyBatis / Doma の実装切り替えを変更した場合は、必要に応じてアプリを起動して動作確認してください。

```shell
./gradlew bootRun
```

そのうえで、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- `readme.md` には API の curl 例があります。エンドポイント仕様を変更した場合のみ更新してください。
- MyBatis Generator と Doma CodeGen はファイルを上書きする可能性があるため、必要な場合だけ実行してください。
- Java 21 で動作するコードを書いてください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
