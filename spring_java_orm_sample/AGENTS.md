# AGENTS.md

## プロジェクト概要

このプロジェクトは、Java 21 / Gradle / Spring Boot を使った書籍管理 API のサンプルアプリケーションです。

主な技術要素は以下です。

- Spring Web
- Spring Data JPA
- H2 Database
- Spring Validation
- Spring Security
- springdoc-openapi
- MyBatis Generator

API は `/api/books` 配下にあり、H2 のインメモリデータベースを使用します。初期データは `src/main/resources/data.sql` で投入されます。

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

## ディレクトリ構成

- `src/main/java/com/example/demo/api/controller`: REST Controller
- `src/main/java/com/example/demo/api/request`: リクエスト DTO
- `src/main/java/com/example/demo/api/response`: レスポンス DTO
- `src/main/java/com/example/demo/jpa/entity`: JPA Entity
- `src/main/java/com/example/demo/jpa/repository`: Repository
- `src/main/java/com/example/demo/jpa/service`: Service
- `src/main/java/com/example/demo/converter`: Entity / DTO 変換
- `src/main/java/com/example/demo/config`: Spring 設定、例外ハンドリング
- `src/main/resources/application.yaml`: アプリケーション設定
- `src/main/resources/data.sql`: 初期データ

## 開発方針

- 既存のレイヤー構造を尊重してください。
  - Controller は API 入出力を扱う
  - Service は業務ロジックを扱う
  - Repository は DB アクセスを扱う
  - Converter は Entity / DTO 変換を扱う
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングは避けてください。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にしてください。
- API の入出力には、可能な限り Entity ではなく request / response DTO を使ってください。
- DB を読む・更新する Service メソッドには `@Transactional` を付けてください。
- 更新・削除処理では、既存のロック制御を不用意に変更しないでください。
- 単純な検索や取得は Spring Data JPA の Repository メソッドを優先してください。
- `application.yaml` は、明示的な依頼がない限り H2 インメモリ DB 前提の設定を維持してください。
- Security 設定を変更する場合は、Swagger、Scalar、H2 Console、`/api/books/**` の動作に影響がないか確認してください。

## テスト方針

コード変更後は、基本的に以下を実行してください。

```shell
./gradlew test
```

API、Security、DB 設定を変更した場合は、必要に応じてアプリを起動して動作確認してください。

```shell
./gradlew bootRun
```

そのうえで、curl または Swagger UI / Scalar で対象エンドポイントを確認してください。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- `readme.md` には API の curl 例があります。エンドポイント仕様を変更した場合のみ更新してください。
- MyBatis Generator はファイルを上書きする可能性があるため、必要な場合だけ実行してください。
- Java 21 で動作するコードを書いてください。
- コメントは必要最小限にし、処理の意図が分かりにくい箇所にだけ追加してください。
