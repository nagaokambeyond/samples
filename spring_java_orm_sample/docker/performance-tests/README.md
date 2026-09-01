# k6 API負荷テスト

この負荷テストでは、書籍検索（70%）、書籍取得（25%）、仕入登録（5%）の混合ワークロードを生成します。外部サービスであるOpenBD APIは呼び出しません。スクリプトの`setup()`でJWTを1回取得し、認証が必要な仕入登録リクエストで再利用します。

Docker Composeでは公式の`grafana/k6:2.2.0`イメージを固定して使用します。イメージはローカルに存在しない場合や明示的に`pull`した場合に取得され、通常の`docker compose run`ごとに再ダウンロードされるわけではありません。

持続可能な負荷の暫定判定基準は次のとおりです。

- HTTP失敗率が1%未満
- check成功率が99%より高い
- レスポンスタイムのp95が500ms未満
- レスポンスタイムのp99が1秒未満
- dropped iterationが0件

## テスト対象アプリケーションの起動

`bootRun`ではなく、パッケージ化したアプリケーションを起動します。`loadtest` profileでは、測定結果に影響するAPIリクエスト／レスポンスログ、SQLログ、H2 Console、DevToolsの再起動機能、APIドキュメント用エンドポイントを無効化します。また、ローカルJVM監視用にHikariとTomcatのMBeanを登録します。

```shell
./gradlew bootJar
mkdir -p build/load-test
java -XX:StartFlightRecording=filename=build/load-test/doma.jfr,dumponexit=true,settings=profile \
  -jar build/libs/demo-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=doma,loadtest \
  --server.port=18080
```

この例では、ローカルで別サービスが`8080`番ポートを使用している場合でも衝突しにくいように、テスト対象アプリケーションを`18080`番ポートで起動します。永続化実装を比較する場合は、`doma`を`jpa`、`mybatis`、または`jooq`に置き換えます。インメモリH2のデータとシーケンスを同じ初期状態へ戻すため、測定のたびにアプリケーションを再起動してください。

## Dockerによるk6の実行

リポジトリのルートディレクトリから、バージョンを固定した公式イメージを取得し、スモークテストを実行します。スモークテストでは、ワークロードに含まれる各APIを1回ずつ呼び出します。`docker/performance-tests/compose.yaml`のデフォルト値は、ローカルで試しやすいように各負荷テストが約2分以内で終わる設定にしています。

```shell
docker compose -f docker/performance-tests/compose.yaml pull
BASE_URL=http://host.docker.internal:18080 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run --summary-export=/results/smoke.json /work/scripts/smoke.js
```

Docker Desktopでは、コンテナからホスト側のSpring Bootへ接続するために`host.docker.internal`を使用します。上記の起動例に合わせ、`BASE_URL`のデフォルト値は`http://host.docker.internal:18080`にしています。

負荷生成機を別のマシンで動かす場合は、そのマシンから接続できるAPIのURLを明示します。

```shell
BASE_URL=http://192.0.2.10:8080 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run /work/scripts/smoke.js
```

APIとk6を同じ物理ホスト上で実行した結果は、最大性能値として扱わないでください。Dockerは実行環境の再現性を高めますが、負荷生成機とアプリケーション間のCPU、メモリ、ネットワーク競合は防げません。

## 最大性能の探索と確認

最初にRPSを段階的に増やし、おおよその限界点を探します。

```shell
BASE_URL=http://host.docker.internal:18080 \
TARGET_RPS_STAGES=25,50,100 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run --summary-export=/results/capacity-ramp.json /work/scripts/capacity-ramp.js
```

次に、候補となるRPSを短時間維持できるか確認します。デフォルトでは、ウォームアップ10秒、ランプアップ10秒、維持80秒、ランプダウン10秒の合計110秒で実行します。RPSを調整しながら繰り返し、判定基準を満たすRPSの目安を特定します。

```shell
BASE_URL=http://host.docker.internal:18080 \
TARGET_RPS=100 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run --summary-export=/results/capacity-confirm-100.json /work/scripts/capacity-confirm.js
```

段階負荷の設定は、`START_RPS`、`TARGET_RPS_STAGES`、`WARMUP_DURATION`、`RAMP_DURATION`、`STEP_DURATION`、`RAMP_DOWN_DURATION`で変更できます。正式な最大性能確認では、`HOLD_DURATION=10m`のように維持時間を延ばしてから測定してください。k6からVU不足が報告された場合に限り、`PRE_ALLOCATED_VUS`と`MAX_VUS`を増やしてください。`dropped_iterations`が発生した測定結果は、最大性能の確認結果として採用しません。

段階負荷の既定値は`START_RPS=10`、`TARGET_RPS_STAGES=25,50,100`、`WARMUP_DURATION=10s`、`RAMP_DURATION=10s`、`STEP_DURATION=20s`、`RAMP_DOWN_DURATION=10s`です。`vus_max`は利用可能な最大VU数、`vus`は実際に使われたVU数です。応答が十分速ければ実際の最大VUが1のままでも異常ではなく、`dropped_iterations`が0なら少なくともk6側のVU不足は発生していません。

## Soakテスト

確認済みの持続可能なRPSで、短時間の継続負荷を実行します。デフォルトでは、ランプアップ10秒、維持90秒、ランプダウン10秒の合計110秒で実行します。

```shell
BASE_URL=http://host.docker.internal:18080 \
TARGET_RPS=100 \
docker compose -f docker/performance-tests/compose.yaml run --rm k6 \
  run --summary-export=/results/soak-100.json /work/scripts/soak.js
```

正式なSoakテストでは、`SOAK_DURATION=30m`や`SOAK_DURATION=60m`のように維持時間を延ばしてください。

実行中はAPI側と負荷生成側の両方を監視してください。API側ではCPU、RSS、ヒープ、GC停止時間、スレッド数、DBコネクションプール使用状況、ロック失敗を収集します。前述の起動コマンドでは、JVMの動作状況をJFRへ記録します。負荷生成側では`docker stats`を使用してコンテナのCPUとメモリを監視し、負荷生成機が飽和した場合はその測定結果を採用しません。

## テストデータと結果の解釈

- 仕入登録では、`data.sql`に存在する在庫行のいずれかを更新します。トランザクションと行ロックを検証しながら、在庫行の初回INSERT競合が測定へ混入することを防ぎます。
- テスト中は仕入伝票、明細、在庫増減履歴、在庫数量が蓄積・更新されます。次の測定前にアプリケーションを再起動してください。
- 日次ログイン回数の上限が10回であるため、ログインAPIは1回のテストにつき1回だけ呼び出します。`loadtest` profileでは、60分のSoakテストと前後のRamp期間を通して同じJWTを再利用できるよう、有効期限を2時間に設定します。
- 測定結果が表すのは、Spring Boot、選択した永続化実装、インメモリH2を組み合わせた性能です。別のデータベースを使用する環境の性能値としては扱わないでください。
- 測定結果を比較するときは、アプリケーションJAR、Javaオプション、ホスト構成、初期データ、k6イメージ、スクリプトパラメーター、ネットワーク配置を同一条件にしてください。
- `purchase_invoice.id`などの主キー重複が発生した場合は、テーブルの最大IDと`data.sql`の`ALTER SEQUENCE ... RESTART WITH`が示す未使用の次の値を確認してください。
