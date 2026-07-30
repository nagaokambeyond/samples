# 書籍・仕入管理 API 移植向け詳細設計書

## 1. 目的と読み方

本書は、現在の Java 21 / Spring Boot サンプルを根拠に、他言語・他 Web フレームワーク・他 ORM / SQL ライブラリでも同等機能を実装できるよう、API 契約、認証、業務ルール、データモデル、エラー仕様、トランザクションと同時更新要件を実装技術から独立して定義する。

Spring Boot、Spring Security、Bean Validation、JPA、MyBatis、Doma、jOOQ などの実装詳細は後半の「Spring 参考実装対応表」に分離する。移植先では同等の責務を持つ仕組みに置き換えてよいが、エンドポイント、JSON 項目名、入力制約、HTTP status、業務処理順、DB 制約、排他制御は本書に合わせる。

主な根拠:

- API インターフェイス: `docs/openapi.yaml`
- Spring 参考実装の API interface: `src/main/java/com/example/demo/api/*OperationApi.java`
- DTO 制約: `src/main/java/com/example/demo/api/request`, `src/main/java/com/example/demo/api/response`
- 認証・認可: `src/main/java/com/example/demo/config/SecurityConfig.java`, `JwtTokenService.java`, `LoginRateLimitService.java`
- 例外仕様: `src/main/java/com/example/demo/config/GlobalExceptionHandler.java`
- 業務処理: `src/main/java/com/example/demo/*/service`, `src/main/java/com/example/demo/*/validator`, `src/main/java/com/example/demo/*/converter`
- DB スキーマ: `src/main/resources/generator-schema.sql`
- 初期データ・設定値: `src/main/resources/data.sql`, `src/main/resources/application.yaml`

## 2. システム概要

対象システムは、書籍、出版社、ジャンル、販売単価履歴、店舗別在庫、在庫増減履歴、仕入伝票、仕入明細を管理する JSON REST API である。

機能:

- ログイン、Bearer token 発行、ログイン回数制限、回数制限リセット
- 書籍の取得、検索、登録、更新、削除
- 書籍の販売単価履歴追加
- OpenBD API による外部書誌情報取得
- 仕入伝票登録、仕入明細登録、在庫加算、在庫増減履歴登録

現行サンプルは JPA / MyBatis / Doma / jOOQ の複数永続化実装を持つ。移植時は 1 種類の永続化方式でよい。ただし、全永続化実装で共通している仕様を本書の正とする。

## 3. 技術非依存の全体方針

- API は JSON REST API とする。
- エラー応答は `application/problem+json` の Problem Detail 形式とする。
- API 入出力には DB entity を直接使わず、request / response DTO 相当の構造を使う。
- 日付は `YYYY-MM-DD`、日時は ISO 8601 相当の date-time とする。
- 認証は Bearer token とする。サンプルは JWT だが、移植先では同等の署名付きトークンでもよい。
- 書籍取得、書籍検索、OpenBD 書誌取得、ログインは公開 API とする。
- 書籍登録、書籍更新、販売単価履歴追加、書籍削除、仕入登録、ログイン回数制限リセットは Bearer token 必須とする。
- 書き込み処理は単一トランザクションで実行する。
- 更新・削除・在庫加算・販売単価履歴追加では、行ロックまたは楽観ロックで競合を検出する。
- 同一処理内で使う現在日時は、可能な限り 1 回取得して使い回す。

## 4. 認証・認可仕様

### 4.1 ログイン認証

ログイン API はユーザー名とパスワードを受け取り、認証成功時に Bearer token を返す。

参考実装の既定値:

| 項目 | 値 |
| --- | --- |
| username | `admin` |
| password | `password` |
| token type | `Bearer` |
| token 署名方式 | HS256 JWT |
| token 有効期限 | 3600 秒 |
| secret 設定キー | `app.auth.jwt-secret` |

移植要件:

- token は改ざん検知できること。
- token には認証主体を識別できる情報を含めること。
- token 有効期限切れ、署名不正、形式不正は認証失敗として扱うこと。
- 認証必須 API では `Authorization: Bearer <token>` を要求すること。

### 4.2 公開 API

| Method | Path |
| --- | --- |
| POST | `/api/auth/login` |
| GET | `/api/books/{id}` |
| GET | `/api/books/search` |
| GET | `/api/books/openbd` |

### 4.3 認証必須 API

| Method | Path |
| --- | --- |
| POST | `/api/auth/login-rate-limit/reset` |
| POST | `/api/books/create` |
| POST | `/api/books/update` |
| POST | `/api/books/{id}/sales-unit-prices` |
| DELETE | `/api/books/{id}` |
| POST | `/api/purchases/create` |

### 4.4 ログイン回数制限

- ユーザー名単位でログイン試行回数を日次カウントする。
- 判定日は設定されたタイムゾーンのローカル日付とする。
- 参考実装の既定上限は 10 回、タイムゾーンは `Asia/Tokyo`。
- ログイン処理では、認証前に回数を消費する。
- 日付が変わった場合、そのユーザーのカウンタは 1 回目から再開する。
- 上限超過時は HTTP 429 を返す。
- リセット API は全ユーザーのカウントを消去し、204 No Content を返す。
- 回数制限機能は設定で無効化できる。無効時はカウントしない。

## 5. API 契約

API インターフェイスの正は `docs/openapi.yaml` とする。本章は移植実装時に読みやすいよう、OpenAPI の path、request schema、response schema、主要 error response を業務仕様の観点で再掲したものである。

Spring 参考実装の `AuthOperationApi`、`BooksOperationApi`、`OpenBdBooksApi`、`PurchaseOperationApi` は `docs/openapi.yaml` に対応する実装側 interface として扱う。移植先で API 仕様を確認・生成する場合は、まず `docs/openapi.yaml` を参照する。

### 5.1 共通

- 成功時の通常レスポンスは `application/json`。
- エラー時は `application/problem+json`。
- ID は 64 bit 整数相当。
- 金額は整数で扱う。消費税や小数通貨の扱いは現行コードにない。
- ページ番号は 0 始まり。

### 5.2 ログイン

`POST /api/auth/login`

Request:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| username | string | yes | 空白不可 |
| password | string | yes | 空白不可 |

Response:

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| username | string | yes | 認証ユーザー名 |
| tokenType | string | yes | 固定値 `Bearer` |
| accessToken | string | yes | Bearer token |
| expiresIn | number | yes | 有効期限秒数 |

エラー:

| HTTP | title | 条件 |
| --- | --- | --- |
| 400 | リクエストバリデーションエラー | request body 不正 |
| 401 | 認証エラー | ユーザー名またはパスワード不正 |
| 429 | リクエスト回数制限 | ログイン日次上限超過 |

### 5.3 ログイン回数制限リセット

`POST /api/auth/login-rate-limit/reset`

- Bearer token 必須。
- 成功時は 204 No Content、body なし。
- token なし、不正 token は 401。

### 5.4 書籍取得

`GET /api/books/{id}`

Path:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| id | number | yes | 整数 |

処理:

1. book を ID で取得する。
2. `publisher`、`book_genre`、現在有効な `book_sales_unit_price_history` を内部結合する。
3. `book_stock` と `store` は左外部結合し、在庫がない書籍も返す。
4. 取得行を 1 書籍単位に集約し、`BookResponse` を返す。
5. 対象 book がない場合は 404。

現在販売単価の判定式:

```sql
effective_from <= current_date
and (effective_to is null or current_date <= effective_to)
```

### 5.5 書籍検索

`GET /api/books/search`

Query:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| keyword | string | no | タイトルまたは著者の前方一致。未指定または空文字なら条件なし |
| releaseDateFrom | date | no | `releaseDateTo` と同時指定 |
| releaseDateTo | date | no | `releaseDateFrom` と同時指定 |
| page | number | yes | 0 以上 |

Response は `BookPageResponse`。

検索条件:

- `keyword` は `title` または `author` への前方一致条件とする。
- `keyword` 検索は大文字小文字を無視する。
- `keyword` が null、未指定、空文字、空白のみの場合は keyword 条件を付けない。
- 発売日範囲が指定された場合、`release_date between releaseDateFrom and releaseDateTo` とする。
- `releaseDateFrom` と `releaseDateTo` は両方指定、または両方未指定とする。
- `releaseDateFrom > releaseDateTo` は相関バリデーションエラー。
- 現在販売単価を持つ書籍だけが検索対象になる。

ページング:

- ページサイズはリクエストで受け取らず、設定値 `search.page-size` で管理する。
- 参考実装の既定ページサイズは 10。
- offset は `page * size`。
- `totalPages` は `ceil(totalElements / size)`。`totalElements = 0` の場合は 0。
- 在庫行との結合でページング件数が崩れないよう、先に book をページングしてから `book_stock` / `store` を結合する。
- 一覧取得 query と count query は同じ book 条件を使う。

### 5.6 書籍登録

`POST /api/books/create`

Bearer token 必須。

Request:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| title | string | yes | 1 から 100 文字 |
| author | string | no | 最大 200 文字 |
| releaseDate | date | yes |  |
| publisherId | number | yes | `publisher.id` に存在 |
| genreId | number | yes | `book_genre.id` に存在 |
| isbn | string | yes | 13 桁数字、未使用 |
| salesUnitPrice | number | yes | 1 から 10000 |

処理:

1. `publisherId` の参照存在チェックを行う。
2. `genreId` の参照存在チェックを行う。
3. `isbn` の一意性チェックを行う。
4. `book` を登録する。
5. 登録した `book.id` に対し、初期販売単価履歴を登録する。
6. 初期販売単価履歴は `sales_unit_price = request.salesUnitPrice`、`effective_from = request.releaseDate`、`effective_to = null` とする。
7. 登録後の書籍を、現在販売単価と在庫リストを含む `BookResponse` として返す。

### 5.7 書籍更新

`POST /api/books/update`

Bearer token 必須。

Request:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| id | number | yes | 更新対象 book.id |
| title | string | yes | 1 から 100 文字 |
| author | string | no | 最大 200 文字 |
| releaseDate | date | yes |  |
| publisherId | number | yes | `publisher.id` に存在 |
| genreId | number | yes | `book_genre.id` に存在 |
| isbn | string | yes | 13 桁数字。他書籍で未使用 |
| version | number | yes | 0 以上、現行 version と一致 |

処理:

1. `publisherId` と `genreId` の参照存在チェックを行う。
2. 対象 book を書き込みロック付きで取得する。
3. 対象がなければ 404。
4. request.version と現行 `book.version` を比較する。不一致なら 409。
5. ISBN の一意性チェックを行う。同一 book の ISBN 維持は許可する。
6. `title`、`author`、`releaseDate`、`publisherId`、`genreId`、`isbn` を更新する。
7. 販売単価はこの API では変更しない。
8. 更新後の書籍を `BookResponse` として返す。

### 5.8 販売単価履歴追加

`POST /api/books/{id}/sales-unit-prices`

Bearer token 必須。成功時は 200 OK、body なし。

Path:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| id | number | yes | book.id |

Request:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| salesUnitPrice | number | yes | 1 から 10000 |
| effectiveFrom | date | yes | 未来日 |

処理:

1. 対象 book を書き込みロック付きで取得する。
2. 対象がなければ 404。
3. `book_id = id` かつ `effective_from >= request.effectiveFrom` の後続履歴を `effective_from` 昇順で取得する。
4. 先頭の後続履歴の `effective_from` が request.effectiveFrom と同じ場合、`(book_id, effective_from)` の一意制約違反として 400。
5. `book_id = id` かつ `effective_from < request.effectiveFrom` の直近前履歴を取得する。
6. 前履歴が存在しなければ 404。
7. 前履歴の `effective_to` を `request.effectiveFrom - 1 日` に更新する。
8. 後続履歴がある場合、新履歴の `effective_to` は `後続履歴.effective_from - 1 日` とする。
9. 後続履歴がない場合、新履歴の `effective_to` は null とする。
10. 新しい販売単価履歴を登録する。

業務制約:

- 現行仕様では販売単価履歴追加は未来日のみ許可する。
- 期間重複を避けるため、既存前履歴の終了日を必ず更新する。
- 現行コードでは対象 book をロックするが、販売単価履歴行自体の検索に明示ロックはない。移植先で厳密な同時追加制御が必要なら、同一 book の履歴範囲取得もロックするか、DB 一意制約と再試行で担保する。

### 5.9 書籍削除

`DELETE /api/books/{id}`

Bearer token 必須。成功時は 200 OK、body なし。

処理:

1. 対象 book を書き込みロック付きで取得する。
2. 対象がなければ 404。
3. book を削除する。
4. `book_sales_unit_price_history` は `book_id` 外部キーの cascade delete により削除される。

注意:

- `book_stock`、`purchase_invoice_detail` は book への外部キーを持つが cascade delete ではない。対象 book を参照する在庫・仕入明細が存在する場合の削除可否は DB 制約に依存する。現行コードはこの参照チェックや制約違反の専用ハンドリングを実装していない。
- `book_stock_movement.book_id` は cascade delete。

### 5.10 OpenBD 書誌取得

`GET /api/books/openbd`

Query:

| 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| isbn | string | yes | 13 桁 ISBN、またはカンマ区切りの 13 桁 ISBN |

ISBN 正規表現:

```text
\d{13}(,\d{13})*
```

処理:

1. `isbn` の形式を検証する。
2. OpenBD API の `/v1/get?isbn=...` 相当を呼び出す。
3. 外部 API のレスポンスを内部 API の `OpenBdBookResponse` に変換する。
4. レスポンスが null、空配列、または配列内に null を含む場合は 404。
5. 外部 API 呼び出し自体が失敗した場合は 502。

OpenBD 接続先の参考実装既定値は `https://api.openbd.jp`。

### 5.11 仕入伝票登録

`POST /api/purchases/create`

Bearer token 必須。

Request:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| purchaseInvoiceDate | date | yes |  |
| supplierId | number | yes | `supplier.id` に存在 |
| receivingStoreId | number | yes | `store.id` に存在 |
| details | array | yes | 1 件以上、最大 10 件 |

`details`:

| JSON 項目 | 型 | 必須 | 制約 |
| --- | --- | --- | --- |
| purchaseInvoiceDetailIsbn | string | yes | 13 桁数字、`book.isbn` に存在 |
| purchaseInvoiceDetailUnitPrice | number | yes | 1 から 10000 |
| purchaseInvoiceDetailQuantity | number | yes | 1 から 1000 |

処理:

1. `supplierId` の参照存在チェックを行う。
2. `receivingStoreId` の参照存在チェックを行う。
3. 明細の ISBN がすべて book に存在することを確認し、ISBN から book.id への map を作る。
4. 各明細金額を `purchaseInvoiceDetailUnitPrice * purchaseInvoiceDetailQuantity` で計算する。
5. 伝票金額を明細金額合計で計算する。
6. `purchase_invoice` を登録する。
7. `purchase_invoice.purchase_invoice_type` は `PURCHASE`、`return_purchase_invoice_id` は null とする。
8. 各 `purchase_invoice_detail` を登録する。
9. 各明細に対応する `book_stock_movement` を登録する。
10. 在庫増減履歴は `movement_type = PURCHASE`、`quantity_delta = 明細数量`、`source_type = PURCHASE_INVOICE`、`source_id = purchase_invoice.id`、`source_detail_id = purchase_invoice_detail.id`、`movement_date = purchaseInvoiceDate` とする。
11. 入庫店舗 ID と本 ID の `book_stock` を書き込みロック付きで取得する。
12. 在庫行がなければ新規作成し、数量は明細数量とする。
13. 在庫行があれば `book_stock_quantity += 明細数量` で更新する。
14. 登録した伝票と明細を `PurchaseInvoiceResponse` として返す。

注意:

- 現行 API は仕入登録のみを公開している。仕入返品 API はない。
- `PurchaseDataValidator` には返品元仕入伝票チェック用の処理があるが、現行公開 API からは使われていない。
- 同一リクエスト内に同じ ISBN の明細が複数ある場合、現行コードは明細ごとに在庫加算する。禁止仕様はない。
- 現行 Doma 実装では伝票・明細・在庫増減履歴の登録後に在庫行をロックしている。すべて同一トランザクションなので失敗時はロールバックされる。

## 6. Response DTO

### 6.1 BookResponse

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | yes | 本 ID |
| title | string | yes | タイトル |
| author | string | no | 著者 |
| releaseDate | date | yes | 発売日付 |
| publisherId | number | yes | 出版社 ID |
| publisherName | string | yes | 出版社名 |
| genreId | number | yes | ジャンル ID |
| genreName | string | yes | ジャンル名 |
| isbn | string | yes | ISBN |
| salesUnitPrice | number | yes | 現在販売単価 |
| updateAt | datetime | yes | 更新日時 |
| version | number | yes | バージョン |
| bookStockList | array | yes | 店舗別在庫リスト。該当在庫なしなら空配列 |

### 6.2 BookStockResponse

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | yes | 本在庫 ID |
| bookStockStoreId | number | yes | 店舗 ID |
| storeName | string | yes | 店舗名 |
| bookStockQuantity | number | yes | 在庫数量 |

### 6.3 BookPageResponse

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| content | array | yes | `BookResponse` の配列 |
| page | number | yes | ページ番号 |
| size | number | yes | ページサイズ |
| totalElements | number | yes | 総件数 |
| totalPages | number | yes | 総ページ数 |

### 6.4 PurchaseInvoiceResponse

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | yes | 仕入伝票 ID |
| purchaseInvoiceType | enum string | yes | `PURCHASE` または `RETURN_PURCHASE` |
| returnPurchaseInvoiceId | number | no | 返品元仕入伝票 ID |
| purchaseInvoiceDate | date | yes | 仕入伝票日付 |
| supplierId | number | yes | 仕入先 ID |
| receivingStoreId | number | yes | 入庫店舗 ID |
| purchaseInvoiceAmount | number | yes | 仕入伝票金額 |
| updateAt | datetime | yes | 更新日時 |
| version | number | yes | バージョン |
| detail | array | yes | `PurchaseInvoiceDetailResponse` の配列 |

### 6.5 PurchaseInvoiceDetailResponse

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| id | number | yes | 仕入伝票明細 ID |
| purchaseInvoiceId | number | yes | 仕入伝票 ID |
| purchaseInvoiceDetailBookId | number | yes | 本 ID |
| purchaseInvoiceDetailUnitPrice | number | yes | 明細単価 |
| purchaseInvoiceDetailQuantity | number | yes | 明細数量 |
| purchaseInvoiceDetailAmount | number | yes | 明細金額 |
| updateAt | datetime | yes | 更新日時 |
| version | number | yes | バージョン |

### 6.6 OpenBdBookResponse

OpenBD 由来のレスポンスは以下の 3 ブロックを返す。

| JSON 項目 | 型 | 説明 |
| --- | --- | --- |
| onix | object | JPRO-onix 準拠項目 |
| hanmoto | object | 版元ドットコム独自書誌項目 |
| summary | object | 書誌概要 |

`summary` の主な項目:

| JSON 項目 | 型 | 説明 |
| --- | --- | --- |
| isbn | string | ISBN |
| title | string | 書名 |
| volume | string | 巻号 |
| series | string | シリーズ名 |
| publisher | string | 出版者 |
| pubdate | string | 出版年月日または出版年月 |
| cover | URI string | 書影 URL |
| author | string | 著者名 |

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

### 7.2 カラム定義

#### publisher

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| publisher_name | varchar(100) | no | 出版社名 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

#### book_genre

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| genre_name | varchar(100) | no | ジャンル名 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

#### book

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| title | varchar(100) | no | タイトル |
| author | varchar(200) | yes | 著者 |
| release_date | date | no | 発売日付 |
| publisher_id | bigint | no | FK `publisher.id` |
| genre_id | bigint | no | FK `book_genre.id` |
| isbn | varchar(13) | no | unique。13 桁数字として扱う |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

主要 index: `release_date`, `publisher_id`, `genre_id`, `title`, `author`。

#### supplier

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| supplier_name | varchar(100) | no | 仕入先名 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

#### store

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| store_name | varchar(100) | no | 店舗名 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

#### purchase_invoice

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| purchase_invoice_type | integer | no | CHECK IN (1, 2) |
| return_purchase_invoice_id | bigint | yes | FK `purchase_invoice.id` |
| purchase_invoice_date | date | no | 仕入伝票日付 |
| supplier_id | bigint | no | FK `supplier.id` |
| receiving_store_id | bigint | no | FK `store.id` |
| purchase_invoice_amount | bigint | no | 仕入伝票金額 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

主要 index: `purchase_invoice_type`, `return_purchase_invoice_id`, `purchase_invoice_date`, `supplier_id`, `receiving_store_id`。

#### purchase_invoice_detail

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| purchase_invoice_id | bigint | no | FK `purchase_invoice.id` |
| purchase_invoice_detail_book_id | bigint | no | FK `book.id` |
| purchase_invoice_detail_unit_price | integer | no | 明細単価 |
| purchase_invoice_detail_quantity | integer | no | 明細数量 |
| purchase_invoice_detail_amount | bigint | no | 明細金額 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

主要 index: `purchase_invoice_id`, `purchase_invoice_detail_book_id`。

#### book_sales_unit_price_history

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| book_id | bigint | no | FK `book.id` ON DELETE CASCADE |
| sales_unit_price | integer | no | CHECK 1 から 10000 |
| effective_from | date | no | 有効開始日 |
| effective_to | date | yes | 有効終了日 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

制約:

- unique `(book_id, effective_from)`
- `effective_to is null or effective_from <= effective_to`

主要 index: `book_id`, `effective_from`, `effective_to`。

#### book_stock

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| book_stock_store_id | bigint | no | FK `store.id` |
| book_stock_book_id | bigint | no | FK `book.id` |
| book_stock_quantity | integer | no | 本在庫数量 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

制約:

- unique `(book_stock_store_id, book_stock_book_id)`

主要 index: `book_stock_store_id`, `book_stock_book_id`, `(book_stock_store_id, book_stock_book_id)`。

#### book_stock_movement

| カラム | 型 | Null | 制約・説明 |
| --- | --- | --- | --- |
| id | bigint | no | identity primary key |
| store_id | bigint | no | FK `store.id` ON DELETE CASCADE |
| book_id | bigint | no | FK `book.id` ON DELETE CASCADE |
| movement_type | integer | no | CHECK IN (1..8) |
| quantity_delta | integer | no | 増減数量 |
| source_type | integer | yes | null または CHECK IN (1..4) |
| source_id | bigint | yes | 発生元 ID |
| source_detail_id | bigint | yes | 発生元明細 ID |
| movement_date | date | no | 在庫増減日付 |
| create_at | timestamp | no | 作成日時 |
| update_at | timestamp | no | 更新日時 |
| version | bigint | no | バージョン |

主要 index: `(store_id, book_id)`, `movement_date`, `movement_type`。

### 7.3 監査項目と version

- 新規登録時は `create_at` と `update_at` に同じ現在日時を設定する。
- 更新時は `update_at` を現在日時に更新する。
- `version` は楽観ロック用の数値とする。
- 参考実装の新規 book と販売単価履歴は version 1 で登録する。
- 初期データは version 0 を含むため、既存データの version はそのまま扱う。
- 一部生成 DAO / ORM が version 初期値や increment を管理する。移植時は「version 不一致を 409 にできること」を優先要件とする。

## 8. Enum

### 8.1 PurchaseInvoiceType

| API 論理値 | DB 値 | 説明 |
| --- | --- | --- |
| PURCHASE | 1 | 仕入 |
| RETURN_PURCHASE | 2 | 仕入返品 |

### 8.2 BookStockMovementType

| API / 内部論理値 | DB 値 | 説明 |
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

| API / 内部論理値 | DB 値 | 説明 |
| --- | --- | --- |
| PURCHASE_INVOICE | 1 | 仕入伝票 |
| SALES_ORDER | 2 | 売上伝票 |
| STOCK_ADJUSTMENT | 3 | 在庫調整 |
| STORE_TRANSFER | 4 | 店舗間移動 |

## 9. エラー仕様

### 9.1 Problem Detail 基本形

エラー応答は `application/problem+json` とし、少なくとも以下を返す。

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| status | number | yes | HTTP status |
| title | string | yes | エラー種別 |
| detail | string | no | 詳細メッセージ |
| instance | string | no | リクエストパス |
| errors | array | no | 入力バリデーションエラー時のみ |

`errors` 要素:

| JSON 項目 | 型 | 必須 | 説明 |
| --- | --- | --- | --- |
| field | string | yes | エラー対象フィールド |
| message | string | yes | エラーメッセージ |

### 9.2 エラー対応表

| HTTP | title | detail | 条件 |
| --- | --- | --- | --- |
| 400 | Bad Request | フレームワーク依存 | path / query の型変換失敗など |
| 400 | リクエストエラー | 制約違反メッセージ | query / path parameter の制約違反 |
| 400 | リクエストバリデーションエラー | なし | request body の制約違反 |
| 400 | 相関バリデーション | 例外メッセージ | 発売日 From/To の組み合わせ不正 |
| 400 | データバリデーション | `参照先データが存在しません: table(column=value)` | 外部キー参照先なし |
| 400 | データバリデーション | `一意制約に違反しています: table(column=value)` | ISBN や販売単価履歴の一意制約違反 |
| 401 | 認証エラー | `ユーザー名またはパスワードが不正です` | ログイン失敗 |
| 401 | Unauthorized | `Unauthorized` または `Invalid bearer token` | token なし、不正、期限切れ |
| 404 | 該当データなし | なし | 対象データなし |
| 404 | OpenBD書誌なし | なし | OpenBD に書誌なし |
| 409 | 更新競合 | `他ユーザーによって更新されています` | 楽観ロックまたは悲観ロック競合 |
| 429 | リクエスト回数制限 | `ログインリクエスト回数が日次上限を超えました` | ログイン回数上限超過 |
| 502 | 外部API呼び出しエラー | `OpenBD APIの呼び出しに失敗しました` | OpenBD API 呼び出し失敗 |

### 9.3 相関バリデーションメッセージ

| 条件 | detail |
| --- | --- |
| `releaseDateFrom` と `releaseDateTo` の片方だけ指定 | `発売日付From、発売日付To両方設定してください。` |
| `releaseDateFrom > releaseDateTo` | `発売日付From＜＝発売日付Toにしてください。` |

## 10. トランザクションと同時更新

### 10.1 読み取り

- 書籍取得と検索は read-only transaction 相当で実行する。
- 現在販売単価は DB の現在日付またはアプリケーションの基準日付を一貫して使い、有効期間内の履歴を選択する。
- 書籍取得・検索は在庫行をロックしない。

### 10.2 書き込みトランザクション境界

以下はそれぞれ単一トランザクションで完了させる。

- 書籍登録と初期販売単価履歴登録
- 書籍更新
- 販売単価履歴追加
- 書籍削除
- 仕入伝票登録、仕入明細登録、在庫増減履歴登録、在庫更新

### 10.3 ロックと version

- 書籍更新、販売単価履歴追加、書籍削除では対象 book を書き込みロック付きで取得する。
- 書籍更新では request.version と現行 version を比較し、不一致なら 409。
- ORM / SQL ライブラリの楽観ロック例外も 409 に変換する。
- 仕入登録では、入庫店舗 ID と本 ID の在庫行をロックしてから既存在庫を更新する。
- 在庫行がない場合は insert する。並行 insert による `(store_id, book_id)` 一意制約競合は、移植先では 409 またはリトライで扱うことが望ましい。現行コードに専用の Problem Detail 変換はない。
- ロック取得失敗は 409。参考実装では短いリトライを行う。

### 10.4 推奨リトライ

ロック取得失敗が一時的である DB では、以下の書き込み処理に限定して短いリトライを実装してよい。

- 書籍更新
- 販売単価履歴追加
- 書籍削除
- 仕入登録

リトライ後も失敗する場合は 409 を返す。

## 11. Spring 参考実装対応表

### 11.1 API と Controller

| 技術非依存の責務 | Spring 参考実装 |
| --- | --- |
| API インターフェイス定義 | `docs/openapi.yaml` |
| 書籍 API interface | `BooksOperationApi` |
| 書籍 Controller | `BooksOperationApiController` |
| OpenBD API interface | `OpenBdBooksApi` |
| OpenBD Controller | `OpenBdBooksApiController` |
| 認証 API interface | `AuthOperationApi` |
| 認証 Controller | `AuthOperationApiController` |
| 仕入 API interface | `PurchaseOperationApi` |
| 仕入 Controller | `PurchaseOperationApiController` |
| request DTO | `api/request/*` |
| response DTO | `api/response/*` |
| ISBN 制約 | `api/annotation/Isbn` |
| 発売日範囲相関チェック | `BooksOperationApiControllerValidator` |

### 11.2 認証・認可

| 技術非依存の責務 | Spring 参考実装 |
| --- | --- |
| Bearer token 検証 | `JwtAuthenticationFilter` |
| token 発行・検証 | `JwtTokenService` |
| 認可ルール | `SecurityConfig#securityFilterChain` |
| ユーザー管理 | `SecurityConfig#userDetailsService` の in-memory user |
| パスワードハッシュ | `BCryptPasswordEncoder` |
| ログイン回数制限 | `LoginRateLimitService` |
| ログイン回数制限設定 | `LoginRateLimitProperties`, `application.yaml` |

### 11.3 Service と永続化

| 技術非依存の責務 | Spring 参考実装 |
| --- | --- |
| 書籍ユースケース interface | `BooksOperationService` |
| 仕入ユースケース interface | `PurchaseOperationService` |
| 既定永続化実装 | `doma` profile |
| JPA 書籍実装 | `BooksOperationServiceJPA` |
| MyBatis 書籍実装 | `BooksOperationServiceMybatis` |
| Doma 書籍実装 | `BooksOperationServiceDoma` |
| jOOQ 書籍実装 | `BooksOperationServiceJooq` |
| JPA 仕入実装 | `PurchaseOperationServiceJPA` |
| MyBatis 仕入実装 | `PurchaseOperationServiceMybatis` |
| Doma 仕入実装 | `PurchaseOperationServiceDoma` |
| jOOQ 仕入実装 | `PurchaseOperationServiceJooq` |
| 書籍データ検証 | `BookDataValidatorJPA/Mybatis/Doma/Jooq` |
| 仕入データ検証 | `PurchaseDataValidatorJPA/Mybatis/Doma/Jooq` |
| DTO 変換 | `BookOperationConverter*`, `PurchaseOperationConverter*` |
| ページ計算 | `PageCalculator` |

### 11.4 DB / SQL

| 技術非依存の責務 | Spring 参考実装 |
| --- | --- |
| 共通スキーマ | `src/main/resources/generator-schema.sql` |
| 初期データ | `src/main/resources/data.sql` |
| 現在販売単価 join | 各 `BookCustomDao`, `BookCustomMapper.xml`, `BookRepository`, `BookOperationDsl` |
| book 書き込みロック | `selectByIdWithWriteLock` / `findByIdWithWriteLock` / `forUpdate().noWait()` |
| 在庫行ロック | `selectByStoreIdAndBookIdWithWriteLock` / `findByStoreIdAndBookIdWithWriteLock` / `forUpdate().noWait()` |
| 販売単価履歴前後取得 | `BookSalesUnitPriceHistoryCustomDao`, `BookCustomMapper`, `BookSalesUnitPriceHistoryRepository`, `BookOperationDsl` |
| jOOQ 手書き DSL | `src/main/java/com/example/demo/jooq/dsl` |
| MyBatis 手書き SQL | `src/main/resources/com/example/demo/mybatis/mapper` |
| Doma 手書き SQL | `src/main/resources/META-INF/com/example/demo/doma/dao` |

### 11.5 例外

| 技術非依存のエラー | Spring 参考実装 |
| --- | --- |
| Problem Detail 変換 | `GlobalExceptionHandler` |
| データなし | `RepositoryDataNotfoundException` |
| OpenBD 書誌なし | `OpenBdBookNotFoundException` |
| 外部キー参照なし | `ForeignKeyReferenceNotFoundException` |
| 一意制約違反 | `UniqueConstraintValidationException` |
| 相関バリデーション | `CorrelationValidationFailureException` |
| ログイン回数制限 | `LoginRateLimitExceededException` |
| 楽観ロック競合 | `ObjectOptimisticLockingFailureException` |
| 悲観ロック競合 | `PessimisticLockingFailureException` |
| OpenBD API 呼び出し失敗 | OpenAPI Generator の `ApiException` |

### 11.6 設定値

| 設定 | 参考実装キー | 既定値 |
| --- | --- | --- |
| 既定永続化 profile | `spring.profiles.default` | `doma` |
| DB URL | `spring.datasource.url` | H2 in-memory PostgreSQL mode |
| ページサイズ | `search.page-size` | 10 |
| OpenBD base URL | `openbd.base-url` | `https://api.openbd.jp` |
| 認証ユーザー | `app.auth.username` | `admin` |
| 認証パスワード | `app.auth.password` | `password` |
| JWT secret | `app.auth.jwt-secret` | ローカル開発用文字列 |
| token 有効期限 | `app.auth.expires-in-seconds` | 3600 |
| ログイン回数制限 enabled | `app.auth.login-rate-limit.enabled` | true |
| ログイン日次上限 | `app.auth.login-rate-limit.daily-limit` | 10 |
| ログイン日次判定 timezone | `app.auth.login-rate-limit.zone-id` | `Asia/Tokyo` |

## 12. 移植時の実装指針

1. DB migration と seed data を先に実装する。
2. request / response DTO と入力バリデーションを実装する。
3. Problem Detail error handler を実装する。
4. Bearer token 発行・検証と認可ルールを実装する。
5. 書籍取得・検索を実装し、現在販売単価と在庫リスト集約をテストする。
6. 書籍登録・更新・削除・販売単価履歴追加を実装する。
7. 仕入登録、在庫加算、在庫増減履歴登録を実装する。
8. OpenBD 連携を実装する。
9. API テスト、service テスト、DB 制約テストを追加する。

移植時に守ること:

- Spring のクラス名は参考情報であり、移植先では自然な構成にしてよい。
- DB の数値 enum は API では論理名として扱い、DB では本書の数値に変換する。
- 書き込み系の競合制御を省略しない。
- 書籍検索では在庫 JOIN によってページングが崩れないようにする。
- 外部 API の DTO をそのまま内部 API レスポンスにしない。内部レスポンス構造へ変換する。
- 業務ルールは controller ではなく service / use case 層でテストできるようにする。

## 13. テスト観点

### 13.1 認証

- 正しいユーザー名・パスワードで token が返る。
- tokenType が `Bearer`、expiresIn が設定値どおり。
- 不正な資格情報で 401。
- ログイン回数上限超過で 429。
- 認証必須 API に token なしでアクセスすると 401。
- 不正 token / 期限切れ token で 401。
- ログイン回数制限リセットでカウントがリセットされる。

### 13.2 書籍取得・検索

- ID 指定で、出版社名、ジャンル名、現在販売単価、在庫リストを含むレスポンスが返る。
- 在庫がない書籍でも bookStockList 空配列で返る。
- 存在しない ID は 404。
- 検索で keyword 前方一致が効く。
- keyword 検索は大文字小文字を無視する。
- keyword 未指定または空白で全件条件になる。
- 発売日範囲検索が効く。
- 発売日 From/To の片方だけ指定で 400。
- From > To で 400。
- ページングの totalElements / totalPages / content が正しい。
- 在庫が複数店舗にある書籍でもページング件数が崩れない。

### 13.3 書籍登録・更新・削除

- 登録時に publisher / genre が存在しない場合は 400。
- 登録時に ISBN 重複なら 400。
- 登録時に初期販売単価履歴が作成される。
- 登録後レスポンスに salesUnitPrice が返る。
- 更新時に version 不一致なら 409。
- 更新時に他書籍の ISBN を指定すると 400。
- 更新 API では販売単価が変更されない。
- 削除後に取得すると 404。
- ロック競合時は 409。

### 13.4 販売単価履歴

- 未来日の販売単価履歴を追加できる。
- 過去日または当日は入力バリデーションで 400。
- 既存直近履歴の `effective_to` が新履歴開始日の前日に更新される。
- 後続履歴がある場合、新履歴の `effective_to` が後続履歴開始日の前日になる。
- 同一 `book_id, effective_from` は 400。
- 現在販売単価は現在日が有効期間内の履歴から取得される。
- 対象 book がない場合は 404。
- 前履歴がない場合は 404。
- ロック競合時は 409。

### 13.5 仕入

- supplier が存在しない場合は 400。
- receivingStore が存在しない場合は 400。
- 明細 ISBN が存在しない場合は 400。
- details が空、null、11 件以上の場合は 400。
- 明細単価と数量の範囲外は 400。
- 明細金額は単価 * 数量。
- 伝票金額は明細金額合計。
- 仕入伝票と明細が登録される。
- 既存在庫がある場合は数量が加算される。
- 既存在庫がない場合は在庫行が作成される。
- 在庫増減履歴に `PURCHASE` / `PURCHASE_INVOICE` が登録される。
- 同一リクエスト内に同じ ISBN が複数ある場合、明細ごとに在庫が加算される。
- 在庫更新競合は 409。

### 13.6 OpenBD

- 単一 ISBN で書誌を取得できる。
- カンマ区切り ISBN で複数書誌を取得できる。
- ISBN 形式不正は 400。
- OpenBD レスポンスが null、空配列、配列内 null の場合は 404。
- 外部 API 失敗は 502。

## 14. 不明点・現行コードで未確定の仕様

- 本削除時に `book_stock` や `purchase_invoice_detail` が対象 book を参照している場合の業務上の期待値は未定義。現行 DB では cascade されず、DB 制約違反になる可能性があるが、専用エラー変換はない。
- 在庫数量の下限・上限 CHECK 制約は DB にない。仕入登録では正の数量のみ受け付けるため加算結果は増えるが、在庫テーブル自体の汎用制約は未定義。
- 仕入伝票日付に過去・未来制約はない。
- 書籍発売日に過去・未来制約はない。
- 金額上限は明細単価・数量・件数から実質的に決まるが、伝票金額カラムの業務上限は未定義。
- ログイン回数制限は現行実装ではインメモリ管理であり、プロセス再起動や複数インスタンス間共有の要件は未定義。
- JWT の issuer / audience / refresh token / token revocation は未定義。
- OpenBD API の timeout、retry、circuit breaker は設定されていない。
- 販売単価履歴に「現在有効な履歴が必ず 1 件」という DB 排他制約はない。業務処理と `(book_id, effective_from)` 一意制約で期間を維持している。
- 仕入返品、売上、在庫調整、店舗間移動の enum は存在するが、公開 API と業務処理は未実装。
