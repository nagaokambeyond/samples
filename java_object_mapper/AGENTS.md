# AGENTS.md

## プロジェクト概要

このリポジトリは、Spring Batch 上でオブジェクトマッピングの性能差を比較する Java サンプルです。

- Spring Boot 4.1.1 / Spring Batch 6.0.5
- Doma3 3.14.0 / H2
- ModelMapper 3.2.6
- MapStruct 1.6.3
- Lombok 1.18.46
- GraalVM Native Build Tools 1.1.8

アプリケーションは Doma で生成データを H2 に投入し、4種類の Spring Batch ジョブを実行して、処理時間と records/sec をログ出力します。

## 重要なルール

- Gradle コマンドはリポジトリルートから Gradle Wrapper で実行してください。
- `src/main/generated-doma/java` 配下の Doma-Gen 生成 Entity は手編集しないでください。
- Doma-Gen の Entity は `src/main/resources/schema.sql` から生成します。
- 手書きの Doma DAO は `src/main/java/com/example/javaobjectmapper/doma` に置きます。
- Doma の SQL ファイルは `src/main/resources/META-INF/com/example/javaobjectmapper/doma/PersonDao` に置きます。
- `.gradle/` と `build/` は生成物なので、ソース管理に含めないでください。

## よく使うコマンド

Doma Entity を生成する:

```bash
./gradlew generateDomaEntities
```

テストを実行する:

```bash
./gradlew test
```

JVM でベンチマークを実行する:

```bash
./gradlew bootRun --args='recordCount=100000'
```

GraalVM native executable をビルドする:

```bash
./gradlew nativeCompile
```

native executable を実行する:

```bash
./build/native/nativeCompile/java-object-mapper recordCount=10000
```

## 開発メモ

- `compileJava` は `generateDomaEntities` に依存しているため、通常のビルドでも Doma Entity が再生成されます。
- `schema.sql` を変更した場合は、`./gradlew generateDomaEntities test` を実行し、生成 Entity の差分を確認してください。
- native image 対応は `NativeRuntimeHints` に依存しています。Doma Entity、ModelMapper の変換対象型、実行時に必要な classpath resource を追加した場合は更新してください。
- Java 25/GraalVM では、ModelMapper 内部依存由来の `sun.misc.Unsafe` 警告が出ることがあります。このサンプルでは警告が出ても実行できます。
- native 実行時に Spring JDBC が `sql-error-codes.xml` に関する info ログを出すことがあります。この H2 サンプルでは情報ログとして扱って問題ありません。

## 基本方針

- メソッド内の変数宣言には、`final var` を積極的に使う。
- 未使用なメソッドであれば削除する。
- 自動生成されたファイルの変更は、最小限にする。

## 依存関係とビルド設定

- Spring Boot BOM 管理下の依存は、原則として明示バージョンを追加せず BOM に従う。
- 依存バージョンを更新する場合は、snapshot / milestone / RC ではなく stable release を基本にする。例外的に prerelease を使う場合は理由と検証範囲を明示する。
- 依存更新後は基本的に `./gradlew test` を実行し、最後に `git status --short` と生成コード差分を確認する。

## エージェント向け注意事項

- 作業前に `git status --short` を確認してください。
- ユーザーの未コミット変更を勝手に戻さないでください。
- コミットメッセージを提案・作成する場合は、先頭に `java-object-mapper　` を付け日本語で作成してください。
