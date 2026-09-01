# AGENTS.md

## プロジェクト概要

Java 21 / Gradle / Spring Boot を使った書籍・仕入管理 API のサンプルです。永続化実装として JPA、MyBatis、Doma、jOOQ を切り替えられ、H2 のインメモリデータベースを使用します。現在のデフォルト profile は `doma` です。

API は `/api/auth`、`/api/books`、`/api/purchases` 配下にあります。プロジェクト構成、主要コンポーネント、profile の詳細は `docs/architecture-overview.md`、現在の API 仕様は `docs/api-spec-notes.md` を確認してください。

## 作業別の必須参照先

作業を始める前に、対象に該当する文書をすべて確認してください。

- 実装・修正・レビュー全般: `docs/codex-implementation-rules.md`
- API、認証、request / response DTO、例外応答: `docs/api-spec-notes.md`
- テスト、起動確認、AOT / ネイティブ確認: `docs/testing-guide.md`
- JPA / MyBatis / Doma / jOOQ の共通方針: `docs/persistence/common.md`
- JPA 固有の変更: `docs/persistence/jpa.md`
- MyBatis 固有の変更: `docs/persistence/mybatis.md`
- Doma 固有の変更: `docs/persistence/doma.md`
- jOOQ 固有の変更: `docs/persistence/jooq.md`
- DB スキーマ、共有ドメイン型、主キー採番、生成コード: `docs/persistence/schema-and-codegen.md`
- k6 負荷テスト: `docker/performance-tests/README.md`
- OWASP ZAP 脆弱性診断: `docker/security-tests/README.md`

複数領域にまたがる変更では、関係する文書を組み合わせて確認してください。指示が矛盾する場合は、対象領域に近い、より具体的な指示を優先してください。

## 共通作業規約

- 作業前に `git status --short` を確認する。
- ユーザーの未コミット変更を勝手に戻さない。
- Java 21 で動作するコードを書く。
- Gradle Wrapper を使用する。
- API の入出力には Entity ではなく request / response DTO を使う。
- JPA / MyBatis / Doma / jOOQ のうち1方式を変更する場合でも、他方式との仕様整合性を確認する。
- 全テーブルの主キーはテーブル単位の `*_seq` シーケンスで採番する。IDENTITY や `max(id) + 1` に変更しない。
- 生成コードは直接編集せず、生成元を変更したうえで、必要な場合だけ生成タスクを実行する。
- 更新・削除処理の既存のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しない。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加する。

## 基本コマンド

```shell
./gradlew test
./gradlew bootRun
./gradlew build
```

コード変更後は基本的に `./gradlew test` を実行します。変更内容に応じた追加確認と、通常テストと AOT テストの分離方針は `docs/testing-guide.md` に従ってください。

生成物を更新する意図がある場合だけ、次のタスクを使用します。

```shell
./gradlew runMyBatisGenerator
./gradlew domaCodeGenLocalAll
./gradlew generateJooq
./gradlew syncOpenBdGeneratedSources
```

`compileJava` は `generateJooq` と `syncOpenBdGeneratedSources` に依存するため、通常のビルド後にも生成差分を確認してください。

## コミットメッセージ

コミットメッセージを提案・作成する場合は、先頭に `spring_java_orm　` を付けてください。
