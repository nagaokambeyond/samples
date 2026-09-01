# テスト・確認ガイド

## 基本方針

コード変更後は、基本的に次を実行します。

```shell
./gradlew test
```

`test` タスクは、Spring Boot AOT プラグインが追加する AOT テスト生成物を通常の JVM テストの classpath から除外しています。`sourceSets.test.output`、`sourceSets.main.output`、`configurations.testRuntimeClasspath` から構成する現在の classpath を、理由なく `sourceSets.test.runtimeClasspath` へ戻さないでください。

`./gradlew test` が `UP-TO-DATE` で実テストを再実行しない場合は、必要に応じて次を実行します。

```shell
./gradlew test --rerun-tasks
```

テストが失敗している、または実行できていない状態は完了として扱わず、失敗内容、未実行理由、残作業を明示してください。

## 変更内容別の確認

- API 相関バリデーション: `BooksOperationApiControllerValidatorTest`
- 書籍 API: `BooksOperationApiControllerTest`
- OpenBD API: `OpenBdBooksApiControllerTest`
- 認証・Security: `AuthOperationApiControllerTest`、`AuthOperationApiLoginRateLimitTest`
- ログイン回数制限: `LoginRateLimitServiceTest`
- 仕入 API: `PurchaseOperationApiControllerTest`
- 例外処理: `GlobalExceptionHandlerTest`
- ページ計算: `PageCalculatorTest`
- OpenBD クライアント設定: `OpenBdClientConfigTest`
- ロック再試行: `RetryableOnLockFailureTest`、`LockFailureRetryTest`
- 行ロック: `BookRowLock` 関連テスト

永続化方式ごとの変更では、対応する `BooksOperationService*Test`、`PurchaseOperationService*Test`、`BookDataValidator*Test`、`PurchaseDataValidator*Test` を確認します。Doma の本データバリデーションには専用の `BookDataValidatorDomaTest` がないため、関連 Service テストも確認してください。

主キーシーケンス、採番 SQL、`data.sql` の再開値、仕入登録の flush または在庫ロック順序を変更した場合は、JPA / MyBatis / Doma / jOOQ の `BooksOperationService*Test` と `PurchaseOperationService*Test` をすべて確認します。

request DTO から採番対象 Entity / row への変換を変更した場合は、同じ外部キー値を持つデータを連続登録し、異なる主キーが採番され、明細や関連データに正しい主キーが設定されることを確認します。

## 起動確認

API、Security、DB 設定、profile、永続化実装の切り替えを変更した場合は、必要に応じて次で起動し、curl、Swagger UI、Scalar で対象 API を確認します。

```shell
./gradlew bootRun
```

BootUI dependency または Security の除外設定を変更した場合は、`http://localhost:8080/bootui` が未認証で表示できることを確認します。

## AOT・ネイティブ

`NativeRuntimeHints`、`application-native.yaml`、AOT / GraalVM 設定、通常テストと AOT テストの classpath 分離、ネイティブ実行時に使う DTO、Doma Entity、classpath resource を変更した場合は、次を確認します。

```shell
./gradlew test --rerun-tasks
./gradlew processAot
```

テスト/AOT のタスク依存を変更した場合は、`./gradlew nativeTest --dry-run` で `processTestAot`、`compileAotTestJava`、`aotTestClasses` の経路が維持されていることを確認します。必要に応じて `./gradlew nativeTest` と `./gradlew nativeCompile` を実行します。

ネイティブイメージには Oracle GraalVM 25 を使用し、通常の Java toolchain は Java 21 のまま維持します。`processAot` とネイティブ実行は `doma,native` profile を使用します。

`nativeCompile` で生成した `demo` 実行ファイルは `doma,native` profile で対象 API を確認します。`nativeCompile` は時間がかかるため、ネイティブ対応と無関係な変更では必須としません。

## 負荷試験・脆弱性診断

- k6 負荷テストの実行と結果判定: `docker/performance-tests/README.md`
- OWASP ZAP API 脆弱性診断: `docker/security-tests/README.md`

ZAP Active Scan は変更を伴う攻撃 payload を送るため、共有環境、ステージング環境、本番環境では実行しません。
