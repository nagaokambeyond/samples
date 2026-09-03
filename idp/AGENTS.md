# リポジトリ運用ガイド

## プロジェクト概要

- Keycloak 26.7.3 と MySQL 8.4 を Docker Compose で起動する、Spring Boot 4.1.1 の複数システム SSO・認可サンプルです。
- Gradle の `portal`（`8081`）と `reports`（`8082`）は同じ Keycloak realm を使いますが、別々の Keycloak client と `JSESSIONID` を持ちます。SSO は Keycloak の共有セッションにより成立します。
- Keycloak は既定でホストの `8080` を使用します。競合する場合は `.env` の `KEYCLOAK_PORT` と、両アプリの `KEYCLOAK_ISSUER_URI` を同じポートへ変更します。
- Keycloak の realm、クライアント、検証用ユーザーは `keycloak/realm-export.json` から初回起動時に import されます。既存の MySQL volume がある場合、realm import は再適用されません。
- Java コードは `portal/src` と `reports/src` にだけ置きます。ルートは Gradle 集約設定、Compose、Keycloak realm、ドキュメントを管理します。

## 開発と検証

- JDK 21 以上を使用します。Gradle は必ずリポジトリ同梱の Wrapper（現在 9.7.1）経由で実行し、システムの `gradle` は使いません。
- 全モジュールの検証は `./gradlew test`、個別の検証は `./gradlew :portal:test` または `./gradlew :reports:test` を使います。起動には `./gradlew :portal:bootRun` と `./gradlew :reports:bootRun` を使用します。
- セキュリティテストは、公開・OIDC ログイン済み画面、システム固有のユーザー／管理者 API、相手システムの client role と `azp` を拒否するロール変換を対象にします。
- Docker 構成の確認には `docker compose config` を使用します。realm 定義を変更した場合は、必要に応じて `docker compose down -v` で MySQL volume を初期化してから再確認します。

## 認証・認可の方針

- ブラウザ用の OIDC ログインと `/api/**` の Bearer JWT 認証は、`SecurityConfig` の別々の `SecurityFilterChain` で扱います。この分離を維持してください。
- JWT の `resource_access.<clientId>.roles` は `ROLE_` 接頭辞付きの Spring Security 権限へ変換します。各システムは自分の clientId のロールだけを認可に利用し、さらに `azp` が自分の clientId と一致するトークンだけを受け入れます。
- portal は `springboot-portal` の `PORTAL_USER` / `PORTAL_ADMIN` を、reports は `springboot-reports` の `REPORT_VIEWER` / `REPORT_ADMIN` を使用します。別システムの API パスやロールを混在させません。
- Keycloak の `springboot-portal` と `springboot-reports` は `pkce.code.challenge.method=S256` で Authorization Code フローの PKCE を必須にしています。Password Grant は API テスト専用であり、PKCE の適用対象外です。
- ブラウザのログアウトは RP-Initiated Logout を使用します。Keycloak client の `post.logout.redirect.uris` と各アプリの実際の origin を一致させ、`id_token_hint`、`client_id`、`post_logout_redirect_uri` を Keycloak へ送ります。
- Keycloak 側のロール、ユーザー、クライアント設定を変更する際は、Spring Security 設定、テスト、README の利用手順も整合させます。

## ファイルとドキュメント

- `.env` はローカル専用で Git 管理しません。値の追加・変更は `.env.example` に反映します。`PORTAL_CLIENT_SECRET` と `REPORTS_CLIENT_SECRET` は realm 定義内の対応する client secret と一致させます。サンプルの認証情報は学習用途だけに限定します。
- README は日本語で維持し、構成やネットワーク接続が変わる場合は Mermaid 図も更新します。
- `build/`、`.gradle/`、IDE が生成する `.idea/` は生成物として扱い、手作業で編集・コミットしません。
