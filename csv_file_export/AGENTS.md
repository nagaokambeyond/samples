# リポジトリガイド

## プロジェクト

このリポジトリは、受注一覧のCSVを非同期で生成し、単一CSVを含むZIPとしてダウンロードさせるサンプルです。Spring Boot 4.1.1、Java 21、Doma 3.14.0、H2 2.3.232、OpenCSV 5.12.0を使用しています。

現在のリポジトリ内容は未コミットです。既存のソースコード、生成済みEntity、スキーマ、ドキュメント、Gradle Wrapperは作業ベースラインとして扱い、まとめて破棄または置換しないでください。

## コマンド

コマンドはGradle Wrapper経由で実行します。

```bash
./gradlew test
./gradlew domaCodeGenH2Entity
./gradlew bootRun
./gradlew bootRun --args='--app.export.seed-size=0'
```

標準設定では、`orders` テーブルが空の場合に起動時に100万件のデモデータを1,000件ずつ投入します。簡易的な起動確認には `--app.export.seed-size=0` を使用してください。アプリケーションは `http://localhost:8080` で起動し、デモ利用者は `alice/password` と `bob/password` です。

## データベース、スキーマ、Doma

- H2は `data/csv_export` にファイルとして永続化され、`MODE=MySQL` と `AUTO_SERVER=TRUE` を設定しています。生成ZIPは `data/exports` に保存します。いずれもGitの管理対象外です。
- `src/main/resources/db/migration/V1__initial_schema.sql` がスキーマの正本です。スキーマ変更時は、まずこのファイルを変更してください。Flywayが起動時に適用します。
- H2方言は `src/main/resources/application.yaml` の `doma.dialect: H2` です。別データベース用のプロファイルを追加しない限り変更しません。
- 手書きDAOのtwo-way SQLは `src/main/resources/META-INF/com/example/csvexport/dao/` に置きます。検索・キーセット取得は `OrderDao`、ジョブの排他取得・所有者検索・期限切れ検索は `CsvExportJobDao` が担当します。
- `src/main/java/com/example/csvexport/generated/entity/` はDoma CodeGenの生成先です。H2のシステムテーブル由来のEntityも含まれますが、手編集しないでください。スキーマ変更後は `./gradlew domaCodeGenH2Entity` を実行し、生成物の変更も含めます。
- CSV出力では `(ordered_at, id)` のキーセットページネーションを維持し、`OFFSET` ページネーションに置き換えないでください。

## 非同期出力の動作

- `POST /exports` は検索条件と依頼者を `QUEUED` 状態で保存して、すぐに画面へ戻ります。
- `ExportDispatcher` は2秒ごとに、`FOR UPDATE SKIP LOCKED` を使ってジョブを1件だけ取得し、`RUNNING` にします。`exportExecutor` はコア1・最大2スレッド、キュー容量10です。
- 出力対象は処理開始時の `snapshot_at` 以前に作成された受注に固定します。全件をメモリへ保持せず、標準1,000件単位で取得してZIPエントリへストリーミングします。
- 状態遷移は `QUEUED` → `RUNNING` → `COMPLETED` または `FAILED` です。完了・失敗から1時間後に期限切れジョブとZIPを削除し、削除時に `EXPIRED` を経由します。
- `FileStorage` は現在 `LocalFileStorage` です。一時ファイル `job-{id}.tmp` を成功時に `orders-export-{id}.zip` へ原子的に移動します。ストレージ実装を差し替える場合も、この確定保存の契約を維持してください。

## CSVとZIPの規約

- ZIP内には `orders-YYYYMMDD-HHmmss.csv` を1件だけ格納し、ダウンロード名は `orders-export-{jobId}.zip`、Content-Typeは `application/zip` です。CSV単体のダウンロードは提供しません。
- CSV生成にはOpenCSVの `CSVWriter` を使用します。値はCSVとして引用・エスケープし、改行はCRLFです。
- ZIP内CSVはUTF-8 BOM付きです。`CsvZipExporter.UTF_8_BOM` は、日本語CSVをExcelで開いたときの文字化け対策のために先頭へ書き込みます。
- 先頭文字が `=`、`+`、`-`、`@` のセル値には単一引用符を付加し、表計算ソフトの数式として解釈されることを防ぎます。この処理を削除または緩和しないでください。

## Webと認可

- Spring Securityのフォームログインを使用し、静的CSS以外は認証が必要です。
- `GET /` は検索結果の先頭100件とログイン利用者自身のジョブ履歴を表示します。検索条件は状態、注文日範囲、顧客名部分一致、金額範囲です。
- `GET /api/exports/{id}` はジョブ進捗をJSONで返し、画面は3秒ごとに更新します。`GET /exports/{id}/download` は完了済みZIPを返します。
- ジョブの状態参照とZIPダウンロードは必ず `owner_username` で認可します。他利用者のジョブを参照・取得できる変更を加えないでください。

## 検証とドキュメント

- 変更後は `./gradlew test` を実行してください。
- CSV形式・BOM・数式エスケープ・ZIPエントリ、ジョブ所有者・状態遷移、Doma SQLを変更する場合は、対応する焦点テストを追加または更新してください。既存の主なテストは `CsvRowWriterTest`、`OrderControllerTest`、`OrderDaoH2IntegrationTest` です。
- ユーザーに見える動作、設定、スキーマ、運用上の前提を変更する場合は、`README.md` と `docs/design.md` も更新してください。
