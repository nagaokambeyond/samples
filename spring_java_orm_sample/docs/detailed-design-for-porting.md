# 書籍・仕入管理 API 詳細設計書

## 1. 目的

本書は、現在の Java 21 / Spring Boot 参考実装を、他の言語・Web フレームワーク・永続化ライブラリで再実装するための詳細設計書である。

実装者は本書を主要な仕様として扱い、特定の Java ライブラリに依存する部分は、採用する技術スタックの同等機能へ置き換える。Codex に実装を依頼する場合も、本書を参照仕様として渡すことを想定する。

## 2. システム概要

対象システムは、書籍情報、販売単価履歴、店舗別在庫、仕入伝票を管理する REST API である。

主な機能は以下のとおり。

- 認証トークン発行とログイン回数制限
- 書籍の取得、検索、登録、更新、削除
- 書籍の販売単価履歴追加
- OpenBD API による外部書誌情報取得
- 仕入伝票登録、仕入明細登録、在庫加算、在庫増減履歴登録

現在の参考実装は複数の永続化方式を持つが、再実装時は一つの永続化方式を選んでよい。ただし、API の入出力、業務ルール、例外仕様、トランザクション境界は維持する。

## 3. 技術非依存の設計方針

- API は JSON REST API とする。
- エラー応答は RFC 7807 形式に近い Problem Detail JSON とする。
- API の入出力には DB entity を直接使わず、request / response DTO 相当の構造を使う。
- 書籍取得・検索・OpenBD 書誌取得・ログインは公開 API とする。
- 書籍登録、更新、削除、販売単価履歴追加、仕入登録、ログイン回数制限リセットは Bearer token 必須とする。
- すべての書き込み処理はトランザクション内で実行する。
- 更新・削除・在庫加算・販売単価履歴追加では、可能な限り行ロックまたは楽観ロックで競合を検出する。
- 日時項目はアプリケーション内で一度だけ現在日時を取得し、同一処理内では同じ値を使う。

## 4. 認証・認可

### 4.1 認証方式

- ログイン API でユーザー名とパスワードを検証する。
- 認証成功時、Bearer token を返す。
- token の有効期限は設定値で管理する。参考実装の既定値は 3600 秒。
- ローカルサンプルの既定ユーザーは `admin`、パスワードは `password`。

### 4.2 公開 API

- `POST /api/auth/login`
- `GET /api/books/{id}`
- `GET /api/books/search`
- `GET /api/books/openbd`

### 4.3 認証必須 API

- `POST /api/auth/login-rate-limit/reset`
- `POST /api/books/create`
- `POST /api/books/update`
- `POST /api/books/{id}/sales-unit-prices`
- `DELETE /api/books/{id}`
- `POST /api/purchases/create`

### 4.4 ログイン回数制限

- ユーザー名単位でログイン試行回数を日次カウントする。
- 参考実装の既定上限は 10 回、判定タイムゾーンは `Asia/Tokyo`。
- ログイン処理では、認証前に回数を消費する。
- 上限超過時は HTTP 429 を返す。
- リセット API は全ユーザーのカウントをリセットする。

## 5. API 詳細

### 5.1 ログイン

`POST /api/auth/login`

Request:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| username | string | 必須 | 空白不可 |
| password | string | 必須 | 空白不可 |

Response:

| 項目 | 型 | 説明 |
| --- | --- | --- |
| username | string | 認証ユーザー名 |
| tokenType | string | 固定値 `Bearer` |
| accessToken | string | アクセストークン |
| expiresIn | number | 有効期限秒数 |

主なエラー:

| HTTP | title | 条件 |
| --- | --- | --- |
| 400 | リクエストバリデーションエラー | request body 不正 |
| 401 | 認証エラー | ユーザー名またはパスワード不正 |
| 429 | リクエスト回数制限 | ログイン日次上限超過 |

### 5.2 ログイン回数制限リセット

`POST /api/auth/login-rate-limit/reset`

- Bearer token 必須。
- 成功時は 204 No Content。

### 5.3 書籍取得

`GET /api/books/{id}`

Path:

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 本 ID |

Response は `BookResponse`。

取得時は、書籍本体、出版社名、ジャンル名、現在販売単価、店舗別在庫リストを返す。現在販売単価は、実行日が有効期間内に含まれる販売単価履歴から取得する。

### 5.4 書籍検索

`GET /api/books/search`

Query:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| keyword | string | 任意 | タイトルまたは著者の前方一致。未指定または空文字なら条件なし |
| releaseDateFrom | date | 任意 | `releaseDateTo` と同時指定 |
| releaseDateTo | date | 任意 | `releaseDateFrom` と同時指定 |
| page | number | 必須 | 0 以上、0 始まり |

Response は `BookPageResponse`。

ページサイズはリクエストでは受け取らず、設定値で管理する。参考実装の既定値は 10。

相関バリデーション:

- `releaseDateFrom` と `releaseDateTo` は両方指定、または両方未指定とする。
- `releaseDateFrom <= releaseDateTo` とする。

検索仕様:

- `keyword` はタイトルまたは著者の前方一致条件とする。
- 大文字小文字を無視して検索する。
- 発売日範囲が指定された場合、`release_date` が範囲内の書籍のみ返す。
- 在庫行との結合でページング件数が崩れないよう、先に書籍をページングしてから在庫・店舗を結合する。
- count query と一覧 query は同じ書籍条件を使う。

### 5.5 書籍登録

`POST /api/books/create`

Bearer token 必須。

Request:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| title | string | 必須 | 1 から 100 文字 |
| author | string | 任意 | 最大 200 文字 |
| releaseDate | date | 必須 |  |
| publisherId | number | 必須 | `publisher.id` に存在すること |
| genreId | number | 必須 | `book_genre.id` に存在すること |
| isbn | string | 必須 | 13 桁数字、未使用であること |
| salesUnitPrice | number | 必須 | 1 から 10000 |

処理:

1. publisherId と genreId の参照存在チェックを行う。
2. ISBN の一意性チェックを行う。
3. book を登録する。
4. 登録した book.id に対し、販売単価履歴を登録する。
5. 販売単価履歴の `effective_from` は request.releaseDate、`effective_to` は null とする。
6. 登録後の書籍を `BookResponse` として返す。

### 5.6 書籍更新

`POST /api/books/update`

Bearer token 必須。

Request:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| id | number | 必須 | 更新対象の book.id |
| title | string | 必須 | 1 から 100 文字 |
| author | string | 任意 | 最大 200 文字 |
| releaseDate | date | 必須 |  |
| publisherId | number | 必須 | `publisher.id` に存在すること |
| genreId | number | 必須 | `book_genre.id` に存在すること |
| isbn | string | 必須 | 13 桁数字、他書籍で未使用であること |
| version | number | 必須 | 0 以上、更新対象の現行 version と一致すること |

処理:

1. publisherId と genreId の参照存在チェックを行う。
2. 対象 book を書き込みロック付きで取得する。
3. 対象がなければ 404 とする。
4. request.version と現行 version を比較する。
5. ISBN の一意性チェックを行う。同一 book の ISBN 維持は許可する。
6. book の基本情報を更新する。販売単価はこの API では変更しない。
7. 更新後の書籍を `BookResponse` として返す。

### 5.7 販売単価履歴追加

`POST /api/books/{id}/sales-unit-prices`

Bearer token 必須。成功時は 200 OK、body なし。

Path:

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 本 ID |

Request:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| salesUnitPrice | number | 必須 | 1 から 10000 |
| effectiveFrom | date | 必須 | 未来日 |

処理:

1. 対象 book を書き込みロック付きで取得する。
2. 対象がなければ 404 とする。
3. `effectiveFrom` 以降の販売単価履歴を昇順で取得する。
4. 同一 `book_id, effective_from` が存在する場合は一意制約違反として 400 とする。
5. `effectiveFrom` より前に開始している直近履歴を取得する。
6. 直近履歴が存在しなければ 404 とする。
7. 直近履歴の `effective_to` を `effectiveFrom - 1 日` に更新する。
8. 後続履歴がある場合、新履歴の `effective_to` は後続履歴の `effective_from - 1 日` とする。後続履歴がない場合は null とする。
9. 新しい販売単価履歴を登録する。

現在販売単価の判定式:

```sql
effective_from <= current_date
and (effective_to is null or current_date <= effective_to)
```

### 5.8 書籍削除

`DELETE /api/books/{id}`

Bearer token 必須。成功時は 200 OK、body なし。

処理:

1. 対象 book を書き込みロック付きで取得する。
2. 対象がなければ 404 とする。
3. book を削除する。
4. 販売単価履歴は `book_sales_unit_price_history.book_id` の cascade delete により削除される。

### 5.9 OpenBD 書誌取得

`GET /api/books/openbd`

Query:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| isbn | string | 必須 | 13 桁 ISBN、またはカンマ区切りの 13 桁 ISBN |

処理:

1. `isbn` の形式を検証する。正規表現は `\d{13}(,\d{13})*`。
2. OpenBD API の `/v1/get?isbn=...` 相当を呼び出す。
3. 外部 API のレスポンスを内部の `OpenBdBookResponse` に変換する。
4. レスポンス内に書誌なしを示す null が含まれる場合は 404 とする。
5. 外部 API 呼び出し自体が失敗した場合は 502 とする。

### 5.10 仕入伝票登録

`POST /api/purchases/create`

Bearer token 必須。

Request:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| purchaseInvoiceDate | date | 必須 |  |
| supplierId | number | 必須 | `supplier.id` に存在すること |
| receivingStoreId | number | 必須 | `store.id` に存在すること |
| details | array | 必須 | 1 件以上、最大 10 件 |

`details`:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| purchaseInvoiceDetailIsbn | string | 必須 | 13 桁数字、book.isbn に存在すること |
| purchaseInvoiceDetailUnitPrice | number | 必須 | 1 から 10000 |
| purchaseInvoiceDetailQuantity | number | 必須 | 1 から 1000 |

処理:

1. supplierId の参照存在チェックを行う。
2. receivingStoreId の参照存在チェックを行う。
3. 明細の ISBN がすべて book に存在することを確認し、ISBN から book.id への map を作る。
4. 各明細金額を `単価 * 数量` で計算する。
5. 伝票金額を明細金額合計で計算する。
6. purchase_invoice を登録する。`purchase_invoice_type` は `PURCHASE`、`return_purchase_invoice_id` は null。
7. 各 purchase_invoice_detail を登録する。
8. 各明細に対応する book_stock_movement を登録する。`movement_type` は `PURCHASE`、`source_type` は `PURCHASE_INVOICE`。
9. 入庫店舗と本 ID の book_stock を書き込みロック付きで取得する。
10. 在庫行がなければ新規作成し、数量は明細数量とする。
11. 在庫行があれば `book_stock_quantity += 明細数量` で更新する。
12. 登録した伝票と明細を `PurchaseInvoiceResponse` として返す。

## 6. Response DTO

### 6.1 BookResponse

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 本 ID |
| title | string | 必須 | タイトル |
| author | string | 任意 | 著者 |
| releaseDate | date | 必須 | 発売日付 |
| publisherId | number | 必須 | 出版社 ID |
| publisherName | string | 必須 | 出版社名 |
| genreId | number | 必須 | ジャンル ID |
| genreName | string | 必須 | ジャンル名 |
| isbn | string | 必須 | ISBN |
| salesUnitPrice | number | 必須 | 現在販売単価 |
| updateAt | datetime | 必須 | 更新日時 |
| version | number | 必須 | バージョン |
| bookStockList | array | 必須 | 店舗別在庫リスト |

### 6.2 BookStockResponse

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 本在庫 ID |
| bookStockStoreId | number | 必須 | 店舗 ID |
| storeName | string | 必須 | 店舗名 |
| bookStockQuantity | number | 必須 | 在庫数量 |

### 6.3 BookPageResponse

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| content | array | 必須 | `BookResponse` の配列 |
| page | number | 必須 | ページ番号 |
| size | number | 必須 | ページサイズ |
| totalElements | number | 必須 | 総件数 |
| totalPages | number | 必須 | 総ページ数 |

`totalPages` は `ceil(totalElements / size)` とする。`totalElements` が 0 の場合は 0 とする。

### 6.4 PurchaseInvoiceResponse

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 仕入伝票 ID |
| purchaseInvoiceType | enum | 必須 | `PURCHASE` または `RETURN_PURCHASE` |
| returnPurchaseInvoiceId | number | 任意 | 返品元仕入伝票 ID |
| purchaseInvoiceDate | date | 必須 | 仕入伝票日付 |
| supplierId | number | 必須 | 仕入先 ID |
| receivingStoreId | number | 必須 | 入庫店舗 ID |
| purchaseInvoiceAmount | number | 必須 | 仕入伝票金額 |
| updateAt | datetime | 必須 | 更新日時 |
| version | number | 必須 | バージョン |
| detail | array | 必須 | 仕入伝票明細 |

### 6.5 PurchaseInvoiceDetailResponse

| 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | 必須 | 仕入伝票明細 ID |
| purchaseInvoiceId | number | 必須 | 仕入伝票 ID |
| purchaseInvoiceDetailBookId | number | 必須 | 本 ID |
| purchaseInvoiceDetailUnitPrice | number | 必須 | 明細単価 |
| purchaseInvoiceDetailQuantity | number | 必須 | 明細数量 |
| purchaseInvoiceDetailAmount | number | 必須 | 明細金額 |
| updateAt | datetime | 必須 | 更新日時 |
| version | number | 必須 | バージョン |

## 7. データモデル

### 7.1 テーブル一覧

| テーブル | 説明 |
| --- | --- |
| publisher | 出版社 |
| book_genre | 本ジャンル |
| book | 本 |
| supplier | 仕入先 |
| store | 店舗 |
| purchase_invoice | 仕入伝票 |
| purchase_invoice_detail | 仕入伝票明細 |
| book_sales_unit_price_history | 本販売単価履歴 |
| book_stock | 本在庫 |
| book_stock_movement | 本在庫増減履歴 |

### 7.2 主要制約

- `book.publisher_id` は `publisher.id` を参照する。
- `book.genre_id` は `book_genre.id` を参照する。
- `book.isbn` は一意で、13 桁数字として扱う。
- `purchase_invoice.supplier_id` は `supplier.id` を参照する。
- `purchase_invoice.receiving_store_id` は `store.id` を参照する。
- `purchase_invoice_detail.purchase_invoice_id` は `purchase_invoice.id` を参照する。
- `purchase_invoice_detail.purchase_invoice_detail_book_id` は `book.id` を参照する。
- `book_sales_unit_price_history.book_id` は `book.id` を参照し、book 削除時に cascade delete される。
- `book_sales_unit_price_history` は `(book_id, effective_from)` を一意とする。
- `book_sales_unit_price_history.sales_unit_price` は 1 から 10000。
- `book_sales_unit_price_history.effective_to` は null、または `effective_from <= effective_to`。
- `book_stock` は `(book_stock_store_id, book_stock_book_id)` を一意とする。
- `book_stock_movement.store_id` は `store.id` を参照する。
- `book_stock_movement.book_id` は `book.id` を参照する。

### 7.3 共通監査項目

多くのテーブルは以下を持つ。

| 項目 | 型 | 説明 |
| --- | --- | --- |
| create_at | datetime | 作成日時 |
| update_at | datetime | 更新日時 |
| version | number | 楽観ロック用バージョン |

新規登録時の version は 1 を基本とする。初期データは 0 を含むため、実装では既存データの version をそのまま扱う。

## 8. Enum

### 8.1 PurchaseInvoiceType

| 論理値 | DB 値 | 説明 |
| --- | --- | --- |
| PURCHASE | 1 | 仕入 |
| RETURN_PURCHASE | 2 | 仕入返品 |

### 8.2 BookStockMovementType

| 論理値 | DB 値 | 説明 |
| --- | --- | --- |
| INITIAL_STOCK | 1 | 初期在庫 |
| PURCHASE | 2 | 仕入 |
| SALE | 3 | 売上 |
| RETURN_PURCHASE | 4 | 仕入返品 |
| SALES_RETURN | 5 | 売上返品 |
| STOCK_ADJUSTMENT | 6 | 在庫調整 |
| STORE_TRANSFER_IN | 7 | 店舗間移動入庫 |
| STORE_TRANSFER_OUT | 8 | 店舗間移動出庫 |

### 8.3 BookStockMovementSourceType

| 論理値 | DB 値 | 説明 |
| --- | --- | --- |
| PURCHASE_INVOICE | 1 | 仕入伝票 |
| SALES_ORDER | 2 | 売上伝票 |
| STOCK_ADJUSTMENT | 3 | 在庫調整 |
| STORE_TRANSFER | 4 | 店舗間移動 |

## 9. エラー仕様

エラー応答は `application/problem+json` を返す。

基本項目:

| 項目 | 型 | 説明 |
| --- | --- | --- |
| status | number | HTTP status |
| title | string | エラー種別 |
| detail | string | 詳細メッセージ。ない場合あり |
| instance | string | リクエストパス |
| errors | array | 入力バリデーションエラー時のみ |

エラー対応:

| HTTP | title | 条件 |
| --- | --- | --- |
| 400 | リクエストエラー | query/path parameter の制約違反 |
| 400 | リクエストバリデーションエラー | request body の制約違反 |
| 400 | 相関バリデーション | 発売日付 From/To の組み合わせ不正 |
| 400 | データバリデーション | 外部キー参照先なし、一意制約違反 |
| 401 | 認証エラー | ログイン失敗 |
| 401 | Unauthorized | Bearer token なし、または不正 |
| 404 | 該当データなし | 対象データなし |
| 404 | OpenBD書誌なし | OpenBD に書誌なし |
| 409 | 更新競合 | 楽観ロックまたは悲観ロック競合 |
| 429 | リクエスト回数制限 | ログイン回数上限超過 |
| 502 | 外部API呼び出しエラー | OpenBD API 呼び出し失敗 |

validation error の `errors` 要素:

| 項目 | 型 | 説明 |
| --- | --- | --- |
| field | string | エラー対象フィールド |
| message | string | エラーメッセージ |

## 10. トランザクションとロック

### 10.1 読み取り

- 書籍取得と検索は read-only transaction 相当で実行する。
- 現在販売単価取得では、実行時点の日付を基準に有効履歴を選ぶ。

### 10.2 書き込み

以下は単一トランザクションで実行する。

- 書籍登録と初期販売単価履歴登録
- 書籍更新
- 販売単価履歴追加
- 書籍削除
- 仕入伝票登録、明細登録、在庫増減履歴登録、在庫更新

### 10.3 競合制御

- 書籍更新、販売単価履歴追加、書籍削除では対象 book を書き込みロック付きで取得する。
- 仕入登録では、入庫店舗 ID と本 ID の在庫行をロックしてから更新する。
- ロック失敗または version 不一致は 409 とする。
- 実装言語・DB が対応する場合、ロック失敗時の短いリトライを実装してよい。

## 11. Codex 実装向け指示

Codex に他言語で実装させる場合は、以下の順序で依頼するとよい。

1. 本書を読ませ、採用する言語、Web フレームワーク、ORM または SQL ライブラリ、DB を明示する。
2. 先に DB schema、migration、seed data を実装させる。
3. Request / response DTO、validation、Problem Detail error handler を実装させる。
4. 認証と Bearer token 検証を実装させる。
5. 書籍取得・検索を実装させ、現在販売単価と在庫リスト集約をテストする。
6. 書籍登録・更新・削除・販売単価履歴追加を実装させる。
7. 仕入登録と在庫更新を実装させる。
8. OpenBD 連携を実装させる。
9. API テスト、service テスト、DB 制約テストを追加させる。

Codex への実装依頼では、以下を明示する。

- Spring Boot 参考実装のクラス名は参考情報であり、移植先では自然な構成にしてよい。
- ただし、エンドポイント、JSON 項目名、validation、業務処理順、HTTP status は本書に合わせる。
- DB の数値 enum は、API では論理名として扱い、DB では本書の数値に変換する。
- 書き込み系の競合制御を省略しない。
- 書籍検索では、在庫 JOIN によってページングが崩れない実装にする。
- 生成コードやフレームワーク固有コードより、業務ロジックをテストで固定する。

## 12. テスト観点

### 12.1 認証

- 正しいユーザー名・パスワードで token が返る。
- 不正な資格情報で 401。
- ログイン回数上限超過で 429。
- 認証必須 API に token なしでアクセスすると 401。
- ログイン回数制限リセットでカウントがリセットされる。

### 12.2 書籍

- ID 指定で、出版社名、ジャンル名、現在販売単価、在庫リストを含むレスポンスが返る。
- 存在しない ID は 404。
- 検索で keyword 前方一致、発売日範囲、ページングが効く。
- 発売日 From/To の片方だけ指定で 400。
- From > To で 400。
- 登録時に publisher / genre が存在しない場合は 400。
- 登録時に ISBN 重複なら 400。
- 登録時に初期販売単価履歴が作成される。
- 更新時に version 不一致なら 409。
- 更新時に他書籍の ISBN を指定すると 400。
- 削除後に取得すると 404。

### 12.3 販売単価履歴

- 未来日の販売単価履歴を追加できる。
- 既存の直近履歴の `effective_to` が新履歴開始日の前日に更新される。
- 後続履歴がある場合、新履歴の `effective_to` が後続履歴開始日の前日になる。
- 同一 `book_id, effective_from` は 400。
- 現在販売単価は現在日が有効期間内の履歴から取得される。

### 12.4 仕入

- supplier / store / 明細 ISBN の参照存在チェックが動く。
- 明細金額は単価 * 数量。
- 伝票金額は明細金額合計。
- 仕入伝票と明細が登録される。
- 既存在庫がある場合は数量が加算される。
- 既存在庫がない場合は在庫行が作成される。
- 在庫増減履歴に `PURCHASE` / `PURCHASE_INVOICE` が登録される。
- 在庫更新競合は 409。

### 12.5 OpenBD

- 単一 ISBN で書誌を取得できる。
- カンマ区切り ISBN で複数書誌を取得できる。
- ISBN 形式不正は 400。
- 書誌なしは 404。
- 外部 API 失敗は 502。
