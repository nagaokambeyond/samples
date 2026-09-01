# OWASP ZAP API脆弱性診断

このディレクトリには、ローカルで起動した使い捨てのSpring Bootアプリケーションに対して、認証付きのOWASP ZAP APIスキャンを実行するための設定を置いています。

スキャンでは、`/v3/api-docs`から現在のOpenAPI定義を取り込み、`/api/auth/login`で取得したBearer tokenを付与してActive Scanを実行します。結果はHTML、JSON、SARIF形式で`docker/security-tests/results`に出力されます。

Active Scanは攻撃用のpayloadを送信するため、データの登録・更新・削除が発生する可能性があります。必ず`run-zap.sh`が起動するインメモリH2のローカルアプリだけを対象にしてください。共有環境、ステージング環境、本番環境には実行しないでください。

## 前提

- Java 21
- Docker Desktop、またはDocker Composeを利用できるDocker Engine
- curl

## ローカル実行

リポジトリルートから実行します。

```shell
docker/security-tests/run-zap.sh
```

既定の永続化profileは`doma`です。別の永続化実装で確認する場合は、次のように切り替えます。

```shell
PERSISTENCE_PROFILE=jpa docker/security-tests/run-zap.sh
PERSISTENCE_PROFILE=mybatis docker/security-tests/run-zap.sh
PERSISTENCE_PROFILE=jooq docker/security-tests/run-zap.sh
```

よく使う上書き設定は次のとおりです。

```shell
APP_PORT=18081 \
APP_USERNAME=admin \
APP_PASSWORD=password \
ZAP_MAX_SCAN_DURATION_MINS=30 \
docker/security-tests/run-zap.sh
```

このスクリプトは、boot JARのビルド、`0.0.0.0:${APP_PORT}`でのアプリ起動、`/v3/api-docs`の起動待ち、ログイン、認証必須APIの事前確認、Docker上でのZAP実行をまとめて行います。終了時には、スクリプト自身が起動したアプリケーションプロセスだけを停止します。

指定したポートで既に別のプロセスが待ち受けている場合、スクリプトは既存プロセスを診断せずに失敗します。既存プロセスを停止するか、`APP_PORT`で別ポートを指定してください。

## レポート

レポートは`docker/security-tests/results`配下に生成されます。

- `zap-api.html`
- `zap-api.json`
- `zap-api.sarif`
- `zap-api-sarif.json`
- `app.log`

ZAPがMediumまたはHighのalertを検出した場合、スキャンは終了コード`1`で失敗します。LowとInformationalのalertはレポートには残りますが、ゲート失敗の対象にはしていません。

ローカル診断ではHTTPでアプリを起動するため、`HTTP Only Site`は`zap-api.yaml`のalert filterでInfo扱いにしています。それ以外のMediumまたはHigh alertは失敗扱いのままです。

## 診断範囲

ZAPの診断対象は、ローカルターゲットの`/api/**`に限定しています。外部OpenBDサービスを攻撃しないよう、`/api/books/openbd`は除外しています。Swagger UI、Scalar、BootUI、H2 Console、Actuator画面はOpenAPIベースのAPIスキャンには取り込まれません。

## 誤検知の扱い

最初から広い抑制リストは追加しないでください。まずHTMLまたはJSONレポートで各alertを確認します。誤検知である根拠が明確な場合だけ、alert ID、URL pattern、理由を絞り込んだZAP alert filterを`zap-api.yaml`へ追加してください。

