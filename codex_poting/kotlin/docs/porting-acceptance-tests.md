# 書籍・仕入管理 API 受け入れテスト仕様

## 1. 目的

本書は、書籍・仕入管理 API を他の言語・フレームワークへ移植するときの受け入れテスト仕様である。

Codex に実装を依頼する場合は、先に本書のテストを実装し、その後に API 実装を進めることを推奨する。詳細な業務仕様は `docs/detailed-design-for-porting.md` を正とし、本書は外部から観測できる振る舞いを固定する。

## 2. 前提

### 2.1 実行環境

- API サーバーが起動していること。
- テスト用 DB は各テストまたは各テストクラスの前に初期化すること。
- 初期データは `src/main/resources/data.sql` 相当を投入すること。
- DB schema は `src/main/resources/generator-schema.sql` 相当であること。
- 認証ユーザーは `admin`、パスワードは `password` とする。
- token 有効期限の既定値は 3600 秒とする。
- 検索ページサイズの既定値は 10 とする。
- OpenBD API は外部ネットワークへ直接接続せず、テストではモックまたはスタブすること。

### 2.2 共通検証ルール

- JSON field 名は camelCase とする。
- エラー応答は `application/problem+json` 相当とし、少なくとも `status`、`title` を検証する。
- `instance` はフレームワーク差が出やすいため、存在確認または任意検証でよい。
- validation message はフレームワークやロケール差が出やすいため、原則として `title` と `errors[].field` を検証する。
- `createAt` は API レスポンスに返さない。
- `updateAt`、`accessToken`、採番 ID は動的値として扱い、型と存在を検証する。
- テストで作成した book は、可能な限りテスト後に削除する。

### 2.3 共通ヘルパー

認証必須 API のテストでは、以下のログインで取得した token を使う。

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

以後、`Authorization: Bearer <accessToken>` を付与する。

## 3. 認証・認可

### AUTH-001 ログイン成功

Request:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

期待結果:

- HTTP 200
- `username` が `admin`
- `tokenType` が `Bearer`
- `accessToken` が空でない
- `expiresIn` が 3600

### AUTH-002 ログイン失敗

Request:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "wrong-password"
}
```

期待結果:

- HTTP 401
- `title` が `認証エラー`

### AUTH-003 ログイン request body 不正

Request:

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": " ",
  "password": ""
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `username`、`password` を含む

### AUTH-004 公開 API は token なしでアクセスできる

Request:

```http
GET /api/books/search?page=0
```

期待結果:

- HTTP 200

### AUTH-005 認証必須 API は token なしで 401

Request:

```http
POST /api/books/create
Content-Type: application/json

{
  "title": "認証なし登録",
  "releaseDate": "2026-01-01",
  "publisherId": 1,
  "genreId": 1,
  "isbn": "9784000000601",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 401

### AUTH-006 ログイン回数制限

前提:

- 日次上限を 10 とする。
- テスト前にログイン回数制限カウントをリセットする。

手順:

1. 不正パスワードで `POST /api/auth/login` を 10 回実行する。
2. 11 回目に同じユーザー名でログインする。

期待結果:

- 1 から 10 回目は HTTP 401
- 11 回目は HTTP 429
- `title` が `リクエスト回数制限`

### AUTH-007 ログイン回数制限リセット

前提:

- AUTH-006 と同じ方法で上限超過状態を作る。
- 管理用 token を取得済みとする。

Request:

```http
POST /api/auth/login-rate-limit/reset
Authorization: Bearer <accessToken>
```

期待結果:

- HTTP 204
- その後、正しい資格情報でログインできる

## 4. 書籍取得・検索

### BOOK-001 書籍取得成功

Request:

```http
GET /api/books/1
```

期待結果:

- HTTP 200
- `id` が 1
- `title` が `Spring入門`
- `author` が `Taro`
- `releaseDate` が `2020-01-01`
- `publisherId` が 1
- `publisherName` が `◯◯書房`
- `genreId` が 5
- `genreName` が `工学`
- `isbn` が `0000000000001`
- `salesUnitPrice` が 1200
- `version` が存在する
- `updateAt` が存在する
- `bookStockList` が 3 件
- `bookStockList` に以下を含む
  - `bookStockStoreId` 1、`storeName` `あ駅前店`、`bookStockQuantity` 10
  - `bookStockStoreId` 2、`storeName` `い駅前店`、`bookStockQuantity` 20
  - `bookStockStoreId` 3、`storeName` `う駅前店`、`bookStockQuantity` 30

### BOOK-002 存在しない書籍取得

Request:

```http
GET /api/books/999
```

期待結果:

- HTTP 404
- `title` が `該当データなし`

### BOOK-003 書籍検索 token なし成功

Request:

```http
GET /api/books/search?page=0
```

期待結果:

- HTTP 200
- `page` が 0
- `size` が 10
- `totalElements` が 21
- `totalPages` が 3
- `content` が 10 件

### BOOK-004 keyword 検索

Request:

```http
GET /api/books/search?keyword=spring&page=0
```

期待結果:

- HTTP 200
- `size` が 10
- `content` が 1 件以上
- `content[0].id` が 1
- `content[0].title` が `Spring入門`
- `content[0].genreId` が 5
- `content[0].genreName` が `工学`
- `content[0].isbn` が `0000000000001`

### BOOK-005 keyword は著者にも前方一致する

Request:

```http
GET /api/books/search?keyword=hana&page=0
```

期待結果:

- HTTP 200
- `content` が 1 件以上
- 返却された各要素は `title` または `author` が `hana` で大文字小文字を無視した前方一致になる

### BOOK-006 発売日範囲検索

Request:

```http
GET /api/books/search?releaseDateFrom=2020-02-01&releaseDateTo=2020-02-01&page=0
```

期待結果:

- HTTP 200
- `content` が 10 件
- `totalElements` が 20
- `totalPages` が 2
- 返却された各要素の `releaseDate` が `2020-02-01`

### BOOK-007 page は 0 始まり

Request:

```http
GET /api/books/search?page=1
```

期待結果:

- HTTP 200
- `page` が 1
- `size` が 10
- `content` が 10 件

### BOOK-008 範囲外 page

Request:

```http
GET /api/books/search?page=3
```

期待結果:

- HTTP 200
- `page` が 3
- `totalElements` が 21
- `totalPages` が 3
- `content` が空配列

### BOOK-009 page が負数

Request:

```http
GET /api/books/search?keyword=spring&page=-1
```

期待結果:

- HTTP 400
- `errors[].field` に `page` を含む

### BOOK-010 発売日 From のみ指定

Request:

```http
GET /api/books/search?releaseDateFrom=2020-01-01&page=0
```

期待結果:

- HTTP 400
- `title` が `相関バリデーション`

### BOOK-011 発売日 From が To より後

Request:

```http
GET /api/books/search?releaseDateFrom=2020-01-02&releaseDateTo=2020-01-01&page=0
```

期待結果:

- HTTP 400
- `title` が `相関バリデーション`

## 5. 書籍登録・更新・削除

### BOOK-W-001 書籍登録成功

Request:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "ISBN登録",
  "author": "Jiro",
  "releaseDate": "2026-01-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "9784000000501",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 200
- `id` が採番される
- `title` が `ISBN登録`
- `author` が `Jiro`
- `releaseDate` が `2026-01-01`
- `publisherId` が 1
- `publisherName` が `◯◯書房`
- `genreId` が 5
- `genreName` が `工学`
- `isbn` が `9784000000501`
- `salesUnitPrice` が 1200
- `version` が 1
- `bookStockList` が空配列

DB 検証:

- book が 1 件追加される。
- `book_sales_unit_price_history` に、追加 book の履歴が 1 件作成される。
- 作成された履歴の `sales_unit_price` は 1200。
- `effective_from` は `2026-01-01`。
- `effective_to` は null。

### BOOK-W-002 書籍登録 request body 不正

Request:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "",
  "releaseDate": null,
  "publisherId": null,
  "genreId": null,
  "isbn": null,
  "salesUnitPrice": null
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `title`、`releaseDate`、`publisherId`、`genreId`、`isbn`、`salesUnitPrice` を含む

### BOOK-W-003 書籍登録 ISBN 形式不正

Request:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "ISBN不正",
  "releaseDate": "2026-01-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "invalid",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 400
- `errors[].field` に `isbn` を含む

### BOOK-W-004 書籍登録 ISBN 重複

Request:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "ISBN重複",
  "releaseDate": "2026-01-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "0000000000001",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `book(isbn=0000000000001)` を含む

### BOOK-W-005 書籍登録 外部キー参照先なし

Request:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "出版社なし",
  "releaseDate": "2026-01-01",
  "publisherId": 999,
  "genreId": 5,
  "isbn": "9784000000502",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `publisher(id=999)` を含む

### BOOK-W-006 書籍更新成功

前提:

- BOOK-W-001 と同等の手順でテスト用 book を作成する。
- 作成レスポンスの `id` と `version` を使う。

Request:

```http
POST /api/books/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "id": "<createdBookId>",
  "title": "本更新HTTP更新後",
  "author": "Saburo",
  "releaseDate": "2026-02-01",
  "publisherId": 2,
  "genreId": 5,
  "isbn": "<createdBookIsbn>",
  "version": "<createdBookVersion>"
}
```

期待結果:

- HTTP 200
- `id` が `<createdBookId>`
- `title` が `本更新HTTP更新後`
- `author` が `Saburo`
- `releaseDate` が `2026-02-01`
- `publisherId` が 2
- `isbn` が `<createdBookIsbn>`
- `version` が更新前より増える、または競合検出に使える新しい値になる
- `salesUnitPrice` は登録時の販売単価のまま

### BOOK-W-007 書籍更新 存在しない ID

Request:

```http
POST /api/books/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "id": 999,
  "title": "存在しない本",
  "author": "Saburo",
  "releaseDate": "2026-02-01",
  "publisherId": 2,
  "genreId": 5,
  "isbn": "9784000000599",
  "version": 1
}
```

期待結果:

- HTTP 404
- `title` が `該当データなし`

### BOOK-W-008 書籍更新 version 不一致

前提:

- 更新対象 book が存在する。

Request:

```http
POST /api/books/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "id": 1,
  "title": "version不一致",
  "author": "Taro",
  "releaseDate": "2020-01-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "0000000000001",
  "version": 999999
}
```

期待結果:

- HTTP 409
- `title` が `更新競合`

### BOOK-W-009 書籍削除成功

前提:

- テスト用 book を作成する。

Request:

```http
DELETE /api/books/<createdBookId>
Authorization: Bearer <accessToken>
```

期待結果:

- HTTP 200
- body が空
- その後 `GET /api/books/<createdBookId>` は HTTP 404

### BOOK-W-010 書籍削除 存在しない ID

Request:

```http
DELETE /api/books/999
Authorization: Bearer <accessToken>
```

期待結果:

- HTTP 404
- `title` が `該当データなし`

## 6. 販売単価履歴

### PRICE-001 販売単価履歴追加成功

前提:

- テスト用 book を作成する。
- 作成時の販売単価は 1200。
- 作成時の `releaseDate` は `2026-01-01`。
- `effectiveFrom` はテスト実行日より未来日にする。例: 実行日の 30 日後。

Request:

```http
POST /api/books/<createdBookId>/sales-unit-prices
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "salesUnitPrice": 1500,
  "effectiveFrom": "<futureDate>"
}
```

期待結果:

- HTTP 200
- body が空

DB 検証:

- 追加 book の販売単価履歴が 2 件になる。
- 旧履歴の `effective_to` が `<futureDate> - 1 日` になる。
- 新履歴の `sales_unit_price` が 1500。
- 新履歴の `effective_from` が `<futureDate>`。
- 後続履歴がなければ新履歴の `effective_to` は null。

### PRICE-002 販売単価履歴 request body 不正

Request:

```http
POST /api/books/1/sales-unit-prices
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "salesUnitPrice": 0,
  "effectiveFrom": "<today>"
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `salesUnitPrice`、`effectiveFrom` を含む

### PRICE-003 販売単価履歴追加 token なし

Request:

```http
POST /api/books/1/sales-unit-prices
Content-Type: application/json

{
  "salesUnitPrice": 1500,
  "effectiveFrom": "<futureDate>"
}
```

期待結果:

- HTTP 401

### PRICE-004 販売単価履歴追加 対象 book なし

Request:

```http
POST /api/books/999/sales-unit-prices
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "salesUnitPrice": 1500,
  "effectiveFrom": "<futureDate>"
}
```

期待結果:

- HTTP 404
- `title` が `該当データなし`

### PRICE-005 同一 effectiveFrom の重複

前提:

- テスト用 book を作成する。
- 同じ `<futureDate>` で PRICE-001 を 1 回成功させる。

Request:

```http
POST /api/books/<createdBookId>/sales-unit-prices
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "salesUnitPrice": 1800,
  "effectiveFrom": "<futureDate>"
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `book_sales_unit_price_history` を含む

### PRICE-006 後続履歴がある場合の期間調整

前提:

- テスト用 book を作成する。
- `<futureDate2>` を `<futureDate1>` より後の日付にする。
- 先に `<futureDate2>` で販売単価 1800 の履歴を追加する。

Request:

```http
POST /api/books/<createdBookId>/sales-unit-prices
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "salesUnitPrice": 1500,
  "effectiveFrom": "<futureDate1>"
}
```

期待結果:

- HTTP 200

DB 検証:

- 初期履歴の `effective_to` が `<futureDate1> - 1 日`
- 追加した 1500 の履歴の `effective_to` が `<futureDate2> - 1 日`
- 先に追加した 1800 の履歴の `effective_from` は `<futureDate2>` のまま

## 7. 仕入伝票

### PURCHASE-001 仕入伝票登録成功

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    },
    {
      "purchaseInvoiceDetailIsbn": "0000000000002",
      "purchaseInvoiceDetailUnitPrice": 500,
      "purchaseInvoiceDetailQuantity": 3
    }
  ]
}
```

期待結果:

- HTTP 200
- `id` が採番される
- `purchaseInvoiceType` が `PURCHASE`
- `returnPurchaseInvoiceId` が null
- `purchaseInvoiceDate` が `2026-02-01`
- `supplierId` が 1
- `receivingStoreId` が 2
- `purchaseInvoiceAmount` が 3500
- `detail` が 2 件
- 1 件目の `purchaseInvoiceDetailBookId` が 1
- 1 件目の `purchaseInvoiceDetailAmount` が 2000
- 2 件目の `purchaseInvoiceDetailBookId` が 2
- 2 件目の `purchaseInvoiceDetailAmount` が 1500

DB 検証:

- purchase_invoice が 1 件追加される。
- purchase_invoice_detail が 2 件追加される。
- `book_stock` の store 2 / book 1 の数量が 20 から 22 になる。
- `book_stock` の store 2 / book 2 の数量が 21 から 24 になる。
- `book_stock_movement` が 2 件追加される。
- 追加された movement の `movement_type` は `PURCHASE`。
- 追加された movement の `source_type` は `PURCHASE_INVOICE`。
- 追加された movement の `source_id` は登録した purchase_invoice.id。
- 追加された movement の `quantity_delta` は各明細数量。

### PURCHASE-002 既存在庫なしの場合は在庫行を作成する

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 4,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 200

DB 検証:

- store 4 / book 1 の `book_stock` が新規作成される。
- 作成された在庫数量が 2。
- `book_stock_movement` が 1 件追加される。

### PURCHASE-003 仕入伝票 request body 不正

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": null,
  "supplierId": null,
  "receivingStoreId": null,
  "details": []
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `purchaseInvoiceDate`、`supplierId`、`receivingStoreId`、`details` を含む

### PURCHASE-004 仕入明細項目不正

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": null,
      "purchaseInvoiceDetailUnitPrice": 0,
      "purchaseInvoiceDetailQuantity": 1001
    }
  ]
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `details[0].purchaseInvoiceDetailIsbn`、`details[0].purchaseInvoiceDetailUnitPrice`、`details[0].purchaseInvoiceDetailQuantity` を含む

### PURCHASE-005 仕入明細 ISBN 形式不正

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    },
    {
      "purchaseInvoiceDetailIsbn": "00000000000A1",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `details[0].purchaseInvoiceDetailIsbn`、`details[1].purchaseInvoiceDetailIsbn` を含む

### PURCHASE-006 details 最大件数超過

Request:

- `details` に有効な明細を 11 件指定して `POST /api/purchases/create` を実行する。

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `details` を含む

### PURCHASE-007 仕入先参照先なし

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 999,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `supplier(id=999)` を含む

### PURCHASE-008 入庫店舗参照先なし

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 999,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `store(id=999)` を含む

### PURCHASE-009 明細 ISBN 参照先なし

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "9999999999999",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `book(isbn=9999999999999)` を含む

### PURCHASE-010 token なし

Request:

```http
POST /api/purchases/create
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": [
    {
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 1000,
      "purchaseInvoiceDetailQuantity": 2
    }
  ]
}
```

期待結果:

- HTTP 401

## 8. OpenBD 書誌取得

### OPENBD-001 単一 ISBN 取得成功

OpenBD stub:

- `isbn=9784780802047` に対し、1 件の書誌を返す。
- summary.isbn は `9784780802047`。
- summary.title は `おにぎりレシピ101`。
- onix.RecordReference は `9784780802047`。
- onix.ProductIdentifier.IDValue は `9784780802047`。
- hanmoto.datemodified は `2025-12-26 11:32:36`。

Request:

```http
GET /api/books/openbd?isbn=9784780802047
```

期待結果:

- HTTP 200
- response body は配列
- `body[0].summary.isbn` が `9784780802047`
- `body[0].summary.title` が `おにぎりレシピ101`
- `body[0].onix.RecordReference` が `9784780802047`
- `body[0].onix.ProductIdentifier.IDValue` が `9784780802047`
- `body[0].hanmoto.datemodified` が `2025-12-26 11:32:36`
- stub が受け取った ISBN は `9784780802047`

### OPENBD-002 カンマ区切り ISBN をそのまま外部 API へ渡す

OpenBD stub:

- `isbn=9784780802047,9784003101018` に対し、2 件の書誌を返す。

Request:

```http
GET /api/books/openbd?isbn=9784780802047,9784003101018
```

期待結果:

- HTTP 200
- response body は 2 件の配列
- stub が受け取った ISBN は `9784780802047,9784003101018`

### OPENBD-003 OpenBD 書誌なし null 要素

OpenBD stub:

- `isbn=9784780802047,9784003101018` に対し、配列 `[null, <book>]` を返す。

Request:

```http
GET /api/books/openbd?isbn=9784780802047,9784003101018
```

期待結果:

- HTTP 404
- `title` が `OpenBD書誌なし`

### OPENBD-004 OpenBD response null

OpenBD stub:

- `isbn=9784780802047` に対し、null を返す。

Request:

```http
GET /api/books/openbd?isbn=9784780802047
```

期待結果:

- HTTP 404
- `title` が `OpenBD書誌なし`

### OPENBD-005 OpenBD response 空配列

OpenBD stub:

- `isbn=9784780802047` に対し、空配列を返す。

Request:

```http
GET /api/books/openbd?isbn=9784780802047
```

期待結果:

- HTTP 404
- `title` が `OpenBD書誌なし`

### OPENBD-006 ISBN query 未指定

Request:

```http
GET /api/books/openbd
```

期待結果:

- HTTP 400
- OpenBD stub は呼び出されない

### OPENBD-007 ISBN query 空文字

Request:

```http
GET /api/books/openbd?isbn=
```

期待結果:

- HTTP 400
- OpenBD stub は呼び出されない

### OPENBD-008 ISBN query 形式不正

Request:

```http
GET /api/books/openbd?isbn=9784780802047,invalid
```

期待結果:

- HTTP 400
- OpenBD stub は呼び出されない

### OPENBD-009 OpenBD API 呼び出し失敗

OpenBD stub:

- `isbn=9784780802047` に対して外部 API エラーを発生させる。

Request:

```http
GET /api/books/openbd?isbn=9784780802047
```

期待結果:

- HTTP 502
- `title` が `外部API呼び出しエラー`
- `detail` が `OpenBD APIの呼び出しに失敗しました`

## 9. エラー応答共通仕様

### ERROR-001 request body validation error

任意の request body validation error を発生させる。

期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors` が配列
- 各要素に `field` と `message` が存在する

### ERROR-002 query validation error

例:

```http
GET /api/books/search?page=-1
```

期待結果:

- HTTP 400
- `errors` が配列
- 各要素に `field` と `message` が存在する

### ERROR-003 データなし

例:

```http
GET /api/books/999
```

期待結果:

- HTTP 404
- `title` が `該当データなし`

### ERROR-004 データバリデーション

例:

```http
POST /api/books/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "title": "ISBN重複",
  "releaseDate": "2026-01-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "0000000000001",
  "salesUnitPrice": 1200
}
```

期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` が存在する

### ERROR-005 更新競合

例:

- version 不一致の書籍更新を行う。
- または、テスト用に対象行をロックした状態で更新系 API を呼ぶ。

期待結果:

- HTTP 409
- `title` が `更新競合`
- `detail` が `他ユーザーによって更新されています`

## 10. 実装完了判定

移植実装は、以下を満たしたら受け入れ可能とする。

- 本書のテストケースがすべて成功する。
- OpenBD 系テストは外部ネットワークに依存せず、モックまたはスタブで成功する。
- テストは DB 初期化後に何度でも再実行できる。
- 書き込み系 API のテストは、作成データや DB 更新結果を検証している。
- 認証必須 API と公開 API の境界がテストされている。
- validation、データなし、データバリデーション、認証、更新競合、外部 API 失敗のエラー分類がテストされている。
