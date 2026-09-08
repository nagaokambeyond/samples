# リポジトリガイド

## 概要

`resilient-api-local` は、Nginx、Spring Boot API 2 台、MySQL 9.7、Redis、Java Worker で構成されるローカル Docker Compose サンプルです。API と Worker は Java 21 / Spring Boot 4.1 の同一アプリケーションイメージを共有し、Spring プロファイルで役割を分けます。

## プロジェクト構成

- `src/main/java`: Spring Boot API と Redis Stream Worker のコード。
- `src/main/resources/schema.sql`: Spring Boot の起動時に実行するスキーマ定義。
- `src/test`: 単体テスト。
- `docker`: Dockerfile、Compose 定義、Nginx 設定、Compose 用環境変数テンプレート。

## ビルドとテスト

Docker 経由で Gradle を使います。Maven の設定は追加しないでください。

```bash
docker build -f docker/Dockerfile -t resilient-api-local:dev .
docker compose -f docker/docker-compose.yml up --build
docker compose -f docker/docker-compose.yml ps
```

Docker ビルドでは `gradle --no-daemon test bootJar` を実行します。ホストに公開するサービスは Nginx のみで、URL は `http://localhost:8080` です。

## 実装規約

- API と Worker の機能は同一の Spring Boot アプリケーションに置き、`api` と `worker` のプロファイルで役割を分離します。
- Worker が未コミットのジョブを処理しないよう、Redis Stream へのジョブ公開はデータベーストランザクションのコミット後に行います。
- ジョブ状態は `PENDING` から `COMPLETED` または `FAILED` に遷移します。このサンプルでは失敗ジョブを再試行しません。
- MySQL、Redis、API のポートは Compose ネットワーク内部に閉じます。
- MySQL `9.7.2` を含め、コンテナイメージのバージョンは固定します。

## データベースの変更

このサンプルの依存関係では、Flyway は MySQL 9.7 をサポートしていません。冪等な DDL を `src/main/resources/schema.sql` に追加し、既存の Compose named volume との互換性を維持してください。

## 検証チェックリスト

- `docker compose -f docker/docker-compose.yml config --quiet` が成功することを確認します。
- 起動後にすべてのサービスが healthy になることを確認します。
- `POST /jobs` の後に `GET /jobs` を呼び、`COMPLETED` になることと、`shouldFail: true` を指定した要求が `FAILED` になることを確認します。
- Nginx または API の耐障害性に関わる変更時は、片方の API を停止して、もう一方の API 経由で要求を継続できることを確認します。
