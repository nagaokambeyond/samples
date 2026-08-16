# AGENTS.md

## プロジェクト概要

このディレクトリは、`../docs` の資料に基づいた書籍・仕入管理システムの Go REST API 実装です。

- 言語: Go 1.26
- REST フレームワーク: Gin
- データベース: SQLite
- DB アクセス: `database/sql` + `sqlc` 生成コード
- SQLite driver: `modernc.org/sqlite`
- 認証: JWT Bearer token
- API ドキュメント表示: OpenAPI YAML、Swagger UI、Scalar

API 契約の正は `../docs/openapi.yaml` です。実行時に配信するため、表示用のコピーを `internal/handler/openapi.yaml` に配置しています。
Swagger UI は OpenAPI 3.1 を表示できないため、表示用コピーの `openapi` version は `3.0.3` に落としています。

## よく使うコマンド

サーバー起動:

```bash
go run ./cmd/server
```

テスト実行:

```bash
go test ./...
```

schema または query を変更した後の `sqlc` 再生成:

```bash
go run github.com/sqlc-dev/sqlc/cmd/sqlc@v1.31.1 generate
```

Go コード整形:

```bash
gofmt -w cmd internal
```

## 実行時の URL

既定のサーバー URL:

```text
http://localhost:8080
```

API ドキュメント:

```text
http://localhost:8080/openapi.yaml
http://localhost:8080/swagger/index.html
http://localhost:8080/scalar
```

動作確認例:

```bash
curl 'http://localhost:8080/api/books/search?page=0'
```

## ディレクトリ構成

- `cmd/server`: アプリケーションのエントリポイント。
- `internal/app`: router の組み立てと依存関係の接続。
- `internal/handler`: Gin handler、認証 middleware、OpenAPI/Swagger/Scalar の配信。
- `internal/service`: 業務ロジック、入力バリデーション、DTO、transaction。
- `internal/db`: SQLite schema、seed data、DB 初期化。
- `internal/db/queries.sql`: `sqlc` が読む SQL。
- `internal/dbsqlc`: `sqlc` の生成コード。手で編集しないこと。
- `internal/auth`: JWT とログイン回数制限。
- `internal/openbd`: OpenBD API client。
- `internal/problem`: Problem Detail 形式のエラー応答。

## 実装方針

- API 契約は `../docs/openapi.yaml` と一致させる。
- API 仕様を変えた場合は、`../docs/openapi.yaml` と `internal/handler/openapi.yaml` の両方を更新する。
- `internal/handler/openapi.yaml` は Swagger UI 互換のため、先頭を `openapi: 3.0.3` にする。
- DB 処理を追加・変更する場合は、基本的に `internal/db/queries.sql` を変更してから `sqlc` を再生成する。
- `internal/dbsqlc` 配下の生成コードは手で編集しない。
- 業務ルールは `internal/service` に置き、Gin handler は薄く保つ。
- `internal/service` の実装は利用する handler のまとまりに合わせて分割する。
- 書籍系の validation は `internal/service/book_validation.go` に集約する。
- 仕入系の validation は `internal/service/purchase_validation.go` に分離する。
- validation の共通 helper は `internal/service/validation.go` に置く。
- エラー応答は `internal/problem` を使い、`application/problem+json` の形を維持する。
- 複数テーブルを更新する書き込み処理は、必ず 1 transaction で実行する。
- JSON field 名は OpenAPI に合わせて camelCase にする。
- 変更後は `gofmt` と `go test ./...` を実行する。

## 設定値

環境変数で上書きできます。

- `PORT`: 既定値 `8080`
- `DATABASE_PATH`: 既定値 `./data/app.db`
- `AUTH_USERNAME`: 既定値 `admin`
- `AUTH_PASSWORD`: 既定値 `password`
- `JWT_SECRET`: ローカル開発用の既定 secret
- `JWT_EXPIRES_IN_SECONDS`: 既定値 `3600`
- `SEARCH_PAGE_SIZE`: 既定値 `10`
- `OPENBD_BASE_URL`: 既定値 `https://api.openbd.jp`
- `LOGIN_RATE_LIMIT_ENABLED`: 既定値 `true`
- `LOGIN_RATE_LIMIT_DAILY_LIMIT`: 既定値 `10`
- `LOGIN_RATE_LIMIT_ZONE`: 既定値 `Asia/Tokyo`
- `API_BODY_LOG_ENABLED`: 既定値 `true`
- `API_BODY_LOG_MAX_BYTES`: 既定値 `8192`
- `SQL_LOG_ENABLED`: 既定値 `true`
- `LOG_LEVEL`: 既定値 `debug`。`debug`、`info`、`warn`、`error` を指定できる。
- `LOG_FORMAT`: 既定値 `json`。`text` または `json` を指定できる。
- `TIME_ZONE`: 既定値 `Asia/Tokyo`。業務日付と監査日時の基準timezone。

## テスト方針

- テストでは `internal/db.OpenMemory` を使い、インメモリ SQLite を利用する。
- routing を含む API 挙動は `internal/app` の handler-level test に追加する。
- `internal/handler` のテストは handler の分割単位に合わせて配置する。
- `internal/service` の validation テストは対象ファイルに合わせて `*_validation_test.go` に配置する。
- 受け入れテストは `internal/app/*_acceptance_test.go` にケースID付きで配置する。
- OpenBD 連携のテストは `openbd.Doer` を差し替え、外部通信やローカルport待受に依存させない。
