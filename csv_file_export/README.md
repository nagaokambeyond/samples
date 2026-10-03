# 非同期ZIP CSV出力サンプル

受注一覧を条件検索し、Doma 3でH2からキーセット方式で読み出して、OpenCSVでCSVを生成・ZIP圧縮して返すSpring Boot 4のサンプルです。

## 起動

Java 21以上を用意して、次を実行します。Dockerは不要です。

```bash
./gradlew bootRun
```

初回は空のDBに100万件の受注を1000件ずつ投入するため、完了まで時間がかかります。ブラウザで `http://localhost:8080` を開き、`alice` / `password` または `bob` / `password` でログインします。

検索条件を指定して「この条件でZIPを作成」を押すと、ジョブが登録されます。画面は3秒ごとに状態を更新し、完了後に `orders-export-{jobId}.zip` を取得できます。ZIPにはUTF-8 BOM付きの `orders-YYYYMMDD-HHmmss.csv` が1ファイルだけ入ります。

## Doma Entityの再生成

テーブル定義は `src/main/resources/db/migration/V1__initial_schema.sql` です。H2のインメモリDBを使って、次を実行してください。

```bash
./gradlew domaCodeGenH2Entity
```

生成物は `src/main/java/com/example/csvexport/generated/entity/` に置かれ、Git管理します。手編集せず、スキーマ変更後に再生成してください。検索・ジョブ取得用DAOはtwo-way SQLで手実装しています。

## 設計上のポイント

- 出力要求はDBへ保存し、Webリクエストとは別のExecutorが処理します。
- `ordered_at, id` のキーセットページングでCSVをストリーム出力するため、対象全件をメモリに載せません。
- 出力開始時の`created_at`を上限にして対象を固定します。サンプルの受注は更新しません。
- ZIPは一時ファイルへ書き込み、成功時だけ確定保存します。`FileStorage`をS3実装へ置き換えれば、本番用オブジェクトストレージに移行できます。
- 完了・失敗ジョブとファイルは1時間後に削除されます。

## 検証

```bash
./gradlew test
```

`app.export.seed-size=0` を指定すると、デモデータの投入を抑止できます。
