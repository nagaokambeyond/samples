# 書籍・仕入管理 Go REST API

`../docs` の設計資料をGoへ移植したREST APIです。

- Web framework: Gin
- Database: SQLite (`modernc.org/sqlite`)
- DB access: `database/sql` + sqlc
- Authentication: JWT Bearer token

## 実行方法

```bash
cd golang
LOG_LEVEL=debug LOG_FORMAT=json go run ./cmd/server
```

既定では `http://localhost:8080` で起動し、SQLite databaseを `./data/app.db` に作成します。

動作確認:

```bash
curl 'http://localhost:8080/api/books/search?page=0'
```

ログイン:

```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"password"}'
```

## APIドキュメント

- OpenAPI YAML: `http://localhost:8080/openapi.yaml`
- Swagger UI: `http://localhost:8080/swagger/index.html`
- Scalar: `http://localhost:8080/scalar`

## テスト

```bash
go test ./...
go vet ./...
```

`internal/app/*_acceptance_test.go` は `../docs/porting-acceptance-tests.md` の全84ケースに対応しています。各ケースは独立したSQLite databaseを使用し、OpenBDは外部通信を行わずHTTP clientをstubへ差し替えます。

## sqlc生成

`internal/db/schema.sql` または `internal/db/queries.sql` を変更した場合に実行します。

```bash
go run github.com/sqlc-dev/sqlc/cmd/sqlc@v1.31.1 generate
```

`internal/dbsqlc` は生成コードのため、直接編集しません。

## 環境変数

| 変数 | 既定値 | 内容 |
| --- | --- | --- |
| `PORT` | `8080` | HTTP server port |
| `DATABASE_PATH` | `./data/app.db` | SQLite database path |
| `AUTH_USERNAME` | `admin` | ログインユーザー名 |
| `AUTH_PASSWORD` | `password` | ログインpassword |
| `JWT_SECRET` | local development用 | JWT署名secret |
| `JWT_EXPIRES_IN_SECONDS` | `3600` | JWT有効秒数 |
| `SEARCH_PAGE_SIZE` | `10` | 書籍検索の1page件数 |
| `OPENBD_BASE_URL` | `https://api.openbd.jp` | OpenBD API base URL |
| `LOGIN_RATE_LIMIT_ENABLED` | `true` | ログイン回数制限 |
| `LOGIN_RATE_LIMIT_DAILY_LIMIT` | `10` | ユーザーごとの日次上限 |
| `LOGIN_RATE_LIMIT_ZONE` | `Asia/Tokyo` | 日次上限のtimezone |
| `API_BODY_LOG_ENABLED` | `true` | request／response body log |
| `API_BODY_LOG_MAX_BYTES` | `8192` | body log最大byte数 |
| `SQL_LOG_ENABLED` | `true` | SQL log |
| `LOG_LEVEL` | `debug` | `debug`、`info`、`warn`、`error` |
| `LOG_FORMAT` | `json` | `json` または `text` |
| `TIME_ZONE` | `Asia/Tokyo` | 業務日付と監査日時のtimezone |

password、access tokenなどの機密値はbody logでマスクされます。

## もとにしたリビジョン

```text
7b5ebe2228806b07b9cd6aa04548c03cfee20514
```
