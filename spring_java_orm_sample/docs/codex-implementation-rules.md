# Codex 実装時の追加規約

この文書は実装・修正・レビュー全般の規約を扱います。API の現在仕様は `api-spec-notes.md`、テスト選択は `testing-guide.md`、永続化は `persistence/` 配下を参照してください。

## 基本方針

- 変更は依頼内容に必要な範囲へ限定し、無関係なリファクタリング、命名変更、フォーマット変更を避ける。
- 既存の未コミット変更を勝手に戻さない。
- 小さなサンプルプロジェクトなので、不要な抽象化や大きなリファクタリングを避ける。
- DI は既存コードと同じく Lombok の `@RequiredArgsConstructor` を基本にする。
- メソッド内の変数宣言には `final var` を積極的に使う。
- `LocalDateTime.now()` を同じ処理内で繰り返し呼ばず、必要に応じて値を使い回す。
- request DTO や内部 row など不変にしたい class は Lombok の `@Value` を基本にする。
- response DTO は ModelMapper や converter から setter で組み立てるため、既存コードと同じく Lombok の `@Data` を基本にする。
- API のリクエスト、レスポンス項目にある `List` には `@NotNull` を付ける。
- 未使用のメソッドは削除する。
- コメントは処理の意図が分かりにくい箇所にだけ追加する。
- SQL で副問合せによる記述が必要な場合は共通テーブル式を使用する。

## パッケージと責務

- API 関連は `api`、`api/annotation`、`api/controller`、`api/log`、`api/request`、`api/response`、`api/validator` の役割分担に合わせる。
- API の入出力には Entity ではなく request / response DTO を使う。
- OpenAPI 注釈は `AuthOperationApi`、`BooksOperationApi`、`OpenBdBooksApi`、`PurchaseOperationApi` に集約し、Controller に重複させない。
- 共有ドメイン型は `data/domain`、ページ計算や例外処理補助は `util` に置く。
- 永続化方式固有の converter、validator、Service は各方式の package に置く。
- OpenBD の手書き設定は `openbd/config`、生成コードは `openbd/generated` に置く。
- JPA profile 固有の Spring 設定は `jpa/config` に置き、`@EnableJpaAuditing` は `JpaAuditingConfig` でのみ有効化する。
- jOOQ の手書き SQL / DSL は `jooq/dsl` に集約する。

## 依存関係とビルド

現在の明示バージョンは `architecture-overview.md` と `build.gradle` を確認してください。

- Spring Boot BOM 管理下の依存は、原則として明示バージョンを追加せず BOM に従う。
- 依存更新は stable release を基本とし、prerelease を使う場合は理由と検証範囲を明示する。
- BootUI は開発支援用なので `developmentOnly` のまま維持し、通常の `implementation` に変更しない。
- Spring Boot、jOOQ、OpenAPI Generator などの更新では、通常ビルドでも jOOQ / OpenBD 生成コードが更新される可能性がある。
- 生成差分を直接編集せず、バージョン表記、未使用 import、テンプレート差分など妥当な機械的変更か確認する。
- 依存更新後は `./gradlew test` を実行し、最後に `git status --short` と生成コード差分を確認する。

## API・Security

- API 仕様変更時は API interface、Controller、request / response DTO、validator、`GlobalExceptionHandler`、`docs/api-spec-notes.md`、必要に応じて `README.md` を揃える。
- 日付範囲の相関チェックは `BooksOperationApiControllerValidator` に集約する。
- ページ数と offset は `PageCalculator` を使い、4方式で計算を重複させない。
- Security / JWT / ログイン回数制限の変更時は `SecurityConfig`、`JwtAuthenticationFilter`、`JwtTokenService`、`LoginRateLimitProperties`、`LoginRateLimitService`、`application.yaml`、`GlobalExceptionHandler`、OpenAPI の `bearerAuth` を確認する。
- 認証設定は `application.yaml` の `app.auth` 配下で管理する。
- `/bootui` と `/bootui/**` は Spring Security / JWT filter の対象外とする。
- Actuator の Web 公開は `management.endpoints.web.exposure.include: health` に限定し、`env` などを不用意に公開しない。
- 現在の公開／認証必須 API、DTO 制約、HTTP status は `api-spec-notes.md` を維持する。
- Spring profile や永続化実装の有効化変更時は `application.yaml`、`application-jpa.yaml`、`application-native.yaml` の役割を確認する。

## OpenBD 連携

- 接続先は `application.yaml` の `openbd.base-url` と `OpenBdProperties` で管理する。
- `OpenBdClientConfig` は生成された `ApiClient`、`BooksApi`、`MetadataApi` を Bean として公開する。
- 生成 DTO を API レスポンスとして直接返さず、`OpenBdBookResponse` に変換する。
- OpenBD 入力制約は `OpenBdBooksApi` の Bean Validation と OpenAPI 注釈に集約する。
- 生成コードを変更する場合は生成元の `src/main/resources/openapi/openbd_api_spec.yaml` または生成設定を修正し、`./gradlew syncOpenBdGeneratedSources` を使う。
- クライアント設定変更時は `OpenBdClientConfigTest`、API 変更時は `OpenBdBooksApiControllerTest` を確認する。

## Service・例外

- 共通 Service interface の変更時は JPA / MyBatis / Doma / jOOQ の4実装をすべて更新する。
- DB を読む・更新する Service メソッドには `@Transactional` を付ける。
- データ参照確認と ISBN 一意性確認は各方式の validator に集約する。
- 更新・削除、販売単価履歴、仕入在庫のロックと `@RetryableOnLockFailure` の既存方針を維持する。
- 永続化方式ごとの converter は取得結果または登録用データと response DTO の変換に責務を限定する。
- 例外は既存の `GlobalExceptionHandler` と `api-spec-notes.md` の HTTP status に揃える。
- validation error の `field` / `message` は `ExceptionHandlerUtil` で組み立てる。

## AOT・ネイティブイメージ

- 通常の Java toolchain は Java 21、ネイティブイメージは Oracle GraalVM 25 を使う。
- `processAot` とネイティブ実行は `doma,native` profile を使う。
- profile を変える場合は `graalvmNative.binaries.main.runtimeArgs` と `processAot` の引数を揃える。
- `application-native.yaml` は MyBatis、JPA、jOOQ の自動構成と H2 Console を無効化し、`generator-schema.sql` を初期化スキーマとして使う。
- `DemoApplication` の `@ImportRuntimeHints` で `NativeRuntimeHints` を登録する。
- リフレクションを使う request / response DTO、Doma Entity、OpenBD 生成 DTO を追加・変更した場合は `NativeRuntimeHints.REFLECTION_TYPES` を確認する。
- Doma SQL や実行時 classpath resource を追加・移動した場合は runtime hint の resource pattern を確認する。
- 現在の登録対象は `META-INF/com/example/demo/doma/**/*.sql` と `generator-schema.sql` である。
- 検証コマンドと通常テスト／AOTテストの分離方針は `testing-guide.md` に従う。
