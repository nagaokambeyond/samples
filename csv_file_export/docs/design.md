# 非同期ZIP CSV出力サンプル 設計書

## 1. 目的

受注一覧の検索条件を指定し、HTTPリクエストとは別のバックグラウンド処理でCSVを生成する。生成したCSVは1ファイルのZIPとして保存し、依頼者本人だけがダウンロードできるようにする。

大量データでもアプリケーションメモリを使い切らないよう、受注はキーセットページングで分割して読み出す。

## 2. システム構成

| 区分 | 採用技術 | 役割 |
| --- | --- | --- |
| Webアプリケーション | Spring Boot 4.1.1 / Java 21 / OpenCSV 5.12.0 | 画面、認証、ジョブ制御、CSV・ZIP生成 |
| DBアクセス | Doma 3.14.0 | Entity、DAO、two-way SQL |
| データベース | H2 2.3.232（ファイル永続） | 受注、出力ジョブ、Flyway履歴 |
| スキーマ管理 | Flyway | 起動時のDDL適用 |
| ファイル保存 | `LocalFileStorage` | ZIPの一時保存・確定保存・削除 |
| 画面 | Thymeleaf | 検索、出力依頼、進捗・ダウンロード表示 |

H2のファイルは `./data/csv_export` に作成する。ZIPは `./data/exports` に保存する。いずれも `.gitignore` の対象である。

```text
ブラウザ
  │ 検索・出力依頼・状態確認・ZIP取得
  ▼
Spring Boot
  ├─ OrderController
  ├─ ExportJobService ── Doma DAO ── H2
  ├─ ExportDispatcher ── CsvZipExporter ── LocalFileStorage
  └─ Thymeleaf
```

## 3. データ設計

### `orders`

| 列 | 型 | 説明 |
| --- | --- | --- |
| `id` | BIGINT | 主キー |
| `order_number` | VARCHAR(32) | 注文番号。一意制約あり |
| `customer_name` | VARCHAR(100) | 顧客名 |
| `product_name` | VARCHAR(100) | 商品名 |
| `amount` | DECIMAL(15,2) | 金額 |
| `status` | VARCHAR(20) | NEW / PAID / SHIPPED / CANCELLED |
| `ordered_at` | TIMESTAMP | 注文日時 |
| `created_at` | TIMESTAMP | 作成日時。出力対象時点の固定に使用 |

`(ordered_at, id)`、`(status, ordered_at, id)`、`created_at` の索引を持つ。`ordered_at, id` はCSV出力のキーセットページングに使用する。

### `csv_export_jobs`

| 列 | 型 | 説明 |
| --- | --- | --- |
| `id` | BIGINT IDENTITY | ジョブID |
| `owner_username` | VARCHAR(100) | 依頼者。認可判定に使用 |
| `status` | VARCHAR(20) | ジョブ状態 |
| `filter_json` | VARCHAR(4000) | `OrderFilter`をJSON化した検索条件 |
| `snapshot_at` | TIMESTAMP | ワーカーが処理開始した時刻 |
| `file_path` | VARCHAR(500) | 確定済みZIPの相対パス |
| `record_count` | BIGINT | 出力行数 |
| `error_message` | VARCHAR(1000) | 失敗理由 |
| `created_at` ほか | TIMESTAMP | 受付・開始・完了・期限時刻 |

状態遷移は次のとおり。

```text
QUEUED → RUNNING → COMPLETED → EXPIRED
                  └→ FAILED ───→ EXPIRED
```

## 4. 処理設計

### 4.1 検索・出力依頼

1. 利用者は状態、注文日範囲、顧客名部分一致、金額範囲を指定する。
2. `POST /exports` が検索条件、ログインユーザー、`QUEUED`状態をDBへ保存する。
3. リクエストは待機せず、画面へリダイレクトする。

### 4.2 非同期CSV・ZIP生成

1. `ExportDispatcher` が2秒間隔でキューを確認する。
2. `FOR UPDATE SKIP LOCKED` で未処理ジョブを1件だけ取得し、`RUNNING`へ変更する。
3. `snapshot_at` を記録し、条件と`created_at <= snapshot_at`を使って対象を固定する。
4. `OrderDao.findNextForExport` が `(ordered_at, id)` より後の1,000件を取得する。
5. 各行をUTF-8 BOM付きCSVとして、`ZipOutputStream`の単一エントリへ逐次出力する。
6. 最終ページ後に一時ZIPを確定保存し、件数・ファイルパス・期限を記録して`COMPLETED`へ変更する。
7. 例外時はジョブを`FAILED`にし、エラー概要を保存する。

ZIP内のエントリ名は `orders-YYYYMMDD-HHmmss.csv`、利用者が取得するファイル名は `orders-export-{jobId}.zip` とする。

### 4.3 CSV安全性

- 文字コードはUTF-8、BOM付き。
- OpenCSVがフィールドの引用符付与と二重引用符のエスケープを行う。
- 値が `=`, `+`, `-`, `@` から始まる場合、先頭へ単一引用符を付加してCSVインジェクションを防ぐ。
- CSV全件やZIP全体をメモリに保持しない。

### 4.4 保持期間

完了・失敗ジョブには完了から1時間後の`expires_at`を設定する。定期処理が対象ZIPを削除し、ジョブも削除する。

## 5. 画面・HTTPインターフェース

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/` | 検索画面・先頭100件・自分の出力履歴を表示 |
| POST | `/exports` | 検索条件で出力ジョブを登録 |
| GET | `/api/exports/{id}` | 自分のジョブ状態をJSONで返却。画面が3秒ごとに呼び出す |
| GET | `/exports/{id}/download` | 完了済みZIPを`application/zip`で返却 |

認証はSpring Securityのフォームログインを使用する。デモ利用者は `alice/password` と `bob/password` であり、他者のジョブ状態・ZIPにはアクセスできない。

## 6. Doma・Entity生成

- DDLは `src/main/resources/db/migration/V1__initial_schema.sql` を唯一の定義とする。
- `./gradlew domaCodeGenH2Entity` は、このDDLを読み込んだインメモリH2からEntityを生成する。
- 生成先は `src/main/java/com/example/csvexport/generated/entity/`。生成物はGit管理し、手編集しない。
- 条件検索、キーセット取得、ジョブの排他取得は生成DAOでは表現しきれないため、Doma DAOと`src/main/resources/META-INF/`配下のtwo-way SQLを手実装する。

## 7. 初期データ

アプリケーション起動時、`orders`が空の場合は`OrderSeeder`が決定的な100万件を1,000件単位で投入する。Domaの`@BatchInsert`を使うため、INSERT用のSQLファイルは不要である。

投入を抑止して起動確認する場合は、次を使う。

```bash
./gradlew bootRun --args='--app.export.seed-size=0'
```

## 8. 運用上の注意

- H2は単一プロセスのデモ・開発用途を想定する。本番の大量出力や複数アプリケーションノードでは、MySQLやPostgreSQL等のサーバーDBとオブジェクトストレージへ置き換える。
- 厳密な過去時点の再現が必要で、受注が更新される場合は履歴テーブルまたはDBスナップショットを追加する。
- `FileStorage`をS3実装へ差し替えれば、ローカルZIP保存をオブジェクトストレージへ移行できる。
