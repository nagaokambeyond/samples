# 永続化実装メモ

## 全体方針

- 現在のデフォルト実装は `BookServiceDoma` です。
- `BookService` は JPA / MyBatis / Doma 共通の Service インターフェースです。
- `BookDataValidatorJPA` / `BookDataValidatorMybatis` / `BookDataValidatorDoma` は永続化方式ごとのデータバリデーションを扱います。
- `BookConverter` は projection / 表示向け Entity から response DTO への変換を扱います。
- service内のメソッドで排他をかけてデータを取得する箇所があれば、メソッドに`@RetryableOnLockFailure`をつけてリトライする。
- JPA / MyBatis / Doma のうち1つの実装を変更する場合でも、他の実装で同じ仕様が必要か確認してください。

## Doma

- `src/main/java/com/example/demo/doma/generator` 配下は Doma CodeGen の生成コードです。手作業での編集は避けてください。
- `src/main/resources/META-INF/com/example/demo/doma/generator/dao` 配下の SQL も生成物として扱ってください。
- 手書き SQL は `BookCustomDao` と `src/main/resources/META-INF/com/example/demo/doma/dao` 配下に追加してください。
- Doma の DAO メソッドを追加する場合は、対応する SQL ファイルのパスとメソッド名を揃えてください。
- `BookWithPublisherName` は Doma 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は SQL と `BookConverter` も更新してください。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleContainingIgnoreCase.sql` と `countByTitleContainingIgnoreCase.sql` の両方を更新してください。
- `BookDao` / `PublisherDao` は Doma CodeGen の生成 DAO です。生成元スキーマとの整合性を維持してください。
- Doma CodeGen の対象スキーマを変える場合は、`generator-schema.sql` と `build.gradle` の `domaCodeGen` 設定の整合性を確認してください。
- Doma 側の更新・削除では、Doma の楽観ロック例外を `ObjectOptimisticLockingFailureException` に変換する既存方針を維持してください。

## MyBatis

- `src/main/java/com/example/demo/mybatis/generator` 配下は MyBatis Generator の生成コードです。手作業での編集は避けてください。
- `src/main/resources/com/example/demo/mybatis/generator` 配下の XML も生成物として扱ってください。
- 手書き SQL は `BookCustomMapper` と `BookCustomMapper.xml` に追加してください。
- `BookWithPublisherName` は MyBatis 用の表示向け Entity です。取得・検索レスポンス向けの列を変更する場合は resultMap と `BookConverter` も更新してください。
- 検索では一覧取得 SQL と count SQL を対で扱ってください。条件を変更する場合は `selectByTitleContainingIgnoreCase` と `countByTitleContainingIgnoreCase` の両方を更新してください。
- `BookMapper` / `PublisherMapper` は MyBatis Generator の生成 Mapper です。生成元スキーマとの整合性を維持してください。
- MyBatis Generator の対象スキーマを変える場合は、`generator-schema.sql` と `generatorConfig.xml` の整合性を確認してください。
- `application.yaml` の `mybatis.mapper-locations` は、MyBatis の XML 読み込みに必要です。不用意に変更しないでください。

## JPA

- JPA 実装は `BookServiceJPA`、`BookRepository`、`PublisherRepository`、`Book`、`Publisher` を中心に構成されています。
- JPA 側の検索は Spring Data JPA Repository メソッドまたは明示的な `@Query` を優先してください。
- JPA 側の取得・検索レスポンスは `BookWithPublisherNameProjection` を使います。列を変更する場合は projection、JPQL、`BookConverter` を揃えてください。
- JPA 側のページング検索は `Pageable` と `countQuery` を使います。検索条件を変更する場合は取得クエリと countQuery の条件を揃えてください。
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
  - JPA projection / MyBatis Entity / Doma Entity の `BookWithPublisherName`
  - 各永続化方式の `BookDataValidator*`
  - request / response DTO
  - `BookConverter`
  - `data.sql`
  - `generator-schema.sql`
  - `generatorConfig.xml`
- `generator-schema.sql` は MyBatis Generator と Doma CodeGen の両方で使われます。片方だけを想定した変更にしないでください。
- `release_date` は検索条件と DTO に関係します。変更時は API バリデーションと3つの Service 実装を確認してください。
- `publisher_id` は `publisher` への外部キーです。変更時は初期データ、生成 Mapper/DAO、外部キー参照チェックを確認してください。
- 検索条件を変更する場合は、JPA / MyBatis / Doma の一覧取得と件数取得が同じ条件になるよう確認してください。

## 生成コマンド

生成物を更新する意図がある場合のみ実行してください。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
```

MyBatis Generator と Doma CodeGen はファイルを上書きする可能性があります。実行前後で差分を確認してください。
