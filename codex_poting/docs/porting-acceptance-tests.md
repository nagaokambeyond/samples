# 書籍・仕入管理 API 受け入れテスト仕様

## 1. 目的

本書は、書籍・仕入管理 API を他の言語・フレームワークへ移植するときの受け入れテスト仕様である。

Codex に実装を依頼する場合は、本書のケース ID と自動テストを対応付けてから API 実装を進める。本書は HTTP から観測できる振る舞いに加え、書き込み処理の DB 更新、トランザクション、ロック競合、ロールバックを固定する。

### 1.1 参照資料と優先順位

仕様が重複する場合は、次の順で判断する。

1. `docs/openapi.yaml`: HTTP method、path、認証要否、request / response schema、status code
2. `docs/detailed-design-for-porting.md`: Doma Service を基準とした処理順、検証、トランザクション、ロック、例外
3. `src/main/resources/generator-schema.sql`: テーブル、列、外部キー、一意制約、version
4. `src/main/resources/data.sql`: 初期データ
5. 本書: 移植実装の受け入れ条件

既知の差異として、`PurchaseInvoiceCreateRequest.details` は実装の `@NotEmpty` と詳細設計に合わせて 1 件以上を必須とする。`docs/openapi.yaml` の `minItems: 0` は受け入れ条件として採用せず、将来 OpenAPI 側で `minItems: 1` に合わせるべき仕様差として扱う。

### 1.2 対象範囲

- ログイン、Bearer token 認証、ログイン回数制限、回数制限リセット
- 書籍の取得、検索、登録、更新、削除
- 販売単価履歴追加と有効期間調整
- 仕入伝票、明細、在庫増減履歴、店舗別在庫の一括更新
- OpenBD 書誌取得と外部 API エラー変換
- 入力検証、データ検証、データなし、認証、競合、外部 API 失敗の Problem Detail

次は現行仕様で期待動作が確定していないため、本書で新しい合否条件を定義しない。

- book 削除時に参照データがある場合の専用エラー応答
- OpenBD の timeout、retry、circuit breaker
- 複数アプリケーションインスタンス間でのログイン回数共有
- 未公開の返品、売上、在庫調整、店舗間移動 API

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
- アプリケーションと DB の日付・timezone は一致させ、既定では `Asia/Tokyo` とする。
- 日付境界を検証するケースではテスト用 Clock または等価な時刻固定機構を使用する。固定できない場合は request 直前に `<today>` を取得し、日付変更をまたいだ実行を失敗ではなく再実行対象にする。
- ロック競合ケースでは、API が使う接続とは別の DB 接続を用意し、明示的なトランザクションで対象行のロックを保持できること。
- DB 障害を注入するケースでは、テスト専用 stub、failpoint、または対象 DAO / Repository の test double を使用してよい。本番用 API や DB schema にテスト専用機能を追加してはならない。

### 2.2 テスト分離と固定データ

- 各ケースは schema 作成と初期データ投入の完了した同一基準状態から開始する。
- ケース間で作成 ID、token、ログイン回数、履歴、在庫を共有しない。
- ケースは単独、任意順、連続 2 回のいずれでも同じ結果になること。
- DB 検証は API と別接続で行い、API のトランザクション完了後に参照する。
- 初期データを直接変更する必要がある場合は、ケースの「事前条件」に INSERT / UPDATE 内容を明記する。
- `<createdBookId>`、`<createdInvoiceId>`、`<accessToken>` などは、そのケース内の準備手順または response から取得する。
- `<futureDate>` は `<today> + 30 日`、`<futureDate1>` は `<today> + 20 日`、`<futureDate2>` は `<today> + 40 日`を既定とする。

### 2.3 共通検証ルール

- JSON field 名は camelCase とする。
- 通常の JSON response は仕様で body がある場合に `application/json`、エラー応答は `application/problem+json` とする。charset parameter の有無は問わない。
- エラー応答では少なくとも body の `status` と実際の HTTP status が一致し、`title` がエラー分類と一致することを検証する。
- `instance` を返す場合は request path と一致すること。フレームワークが省略する場合は許容する。
- validation message はフレームワークやロケール差が出やすいため、原則として `title` と `errors[].field` を検証する。
- `createAt` は API レスポンスに返さない。
- `updateAt`、`accessToken`、採番 ID は動的値として扱い、型と存在を検証する。
- ID は正の 64 bit 整数、version は正の整数、日時は ISO 8601 date-time として解釈できることを検証する。
- リスト順を仕様化しているケースでは配列順も検証する。それ以外では ID など安定キーで比較する。
- 書き込み成功時は response だけでなく、対象テーブルと関連テーブルを検証する。
- 書き込み失敗時は、ケース開始時の DB snapshot と比較して対象テーブルに部分更新がないことを検証する。
- テストで作成したデータはケース終了時にロールバックするか、schema 再作成で破棄する。

### 2.4 ケース記述形式

各ケースは必要に応じて次の項目を持つ。

| 項目 | 内容 |
| --- | --- |
| 目的 | 固定する利用者向け振る舞いまたは業務不変条件 |
| 事前条件 | DB、Clock、stub、token、ロックなどの準備 |
| Request / 入力 | HTTP request または障害注入条件 |
| 手順 | 複数 request や並行処理が必要な場合の順序 |
| HTTP / response 期待結果 | status、content type、body、header |
| DB 検証 | 登録・更新・削除、関連、version、ロールバック |
| 後処理 | ロック解放、Clock 復元、stub リセット、データ破棄 |

既存ケースで項目が省略されている場合は、次の既定を適用する。

- 目的はケース名に記載された振る舞いの確認とする。
- 事前条件は 2.1 と 2.2 の共通状態だけとする。
- 手順は記載された request を 1 回実行するものとする。
- DB 検証がない読み取り・入力エラーケースでは DB が不変であることを確認する。
- 後処理は DB 初期化、Clock 復元、stub リセットとする。

### 2.5 共通ヘルパー

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

期限切れ token はテスト用 Clock を token 発行時刻から 3601 秒進めて作る。署名不正 token は正常 token の payload または signature を 1 文字変更し、別の有効な token として再署名しない。

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

### AUTH-008 正常 token で認証必須 API にアクセスできる

目的:

- ログインで発行した token が実際の認可処理に利用できることを確認する。

事前条件:

- ログインで `<accessToken>` を取得する。

Request:

```http
POST /api/auth/login-rate-limit/reset
Authorization: Bearer <accessToken>
```

HTTP / response 期待結果:

- HTTP 204
- response body は空

### AUTH-009 Bearer token の形式不正

Request:

```http
POST /api/auth/login-rate-limit/reset
Authorization: Basic invalid
```

HTTP / response 期待結果:

- HTTP 401
- 認証エラーとして扱われる
- DB とログイン回数カウンタは変更されない

### AUTH-010 Bearer token の署名不正

事前条件:

- 正常 token の signature を変更した `<tamperedToken>` を用意する。

Request:

```http
POST /api/auth/login-rate-limit/reset
Authorization: Bearer <tamperedToken>
```

HTTP / response 期待結果:

- HTTP 401
- `detail` は `Invalid bearer token` または同等の認証失敗を表す
- ログイン回数カウンタは変更されない

### AUTH-011 Bearer token の期限切れ

事前条件:

- Clock を固定して token を発行する。
- Clock を token 発行時刻から 3601 秒後へ進める。

Request:

```http
POST /api/auth/login-rate-limit/reset
Authorization: Bearer <expiredToken>
```

HTTP / response 期待結果:

- HTTP 401
- 期限切れ token が認証済みとして扱われない
- ログイン回数カウンタは変更されない

後処理:

- Clock を共通基準時刻へ戻す。

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

### BOOK-012 在庫がない書籍を取得できる

事前条件:

- 現在有効な販売単価履歴を持ち、`book_stock` を持たないテスト用 book を登録する。

Request:

```http
GET /api/books/<bookWithoutStockId>
```

HTTP / response 期待結果:

- HTTP 200
- `id` が `<bookWithoutStockId>`
- `bookStockList` が空配列
- 出版社、ジャンル、ISBN、現在販売単価が返る

### BOOK-013 複数店舗の在庫を書籍単位に集約する

事前条件:

- 同じ book に対して 3 店舗分の `book_stock` を、在庫 ID が昇順になるよう登録する。

Request:

```http
GET /api/books/<bookWithThreeStocksId>
```

HTTP / response 期待結果:

- HTTP 200
- 書籍は 1 オブジェクトだけ返る
- `bookStockList` は 3 要素
- 各要素の店舗 ID、店舗名、数量が DB と一致する
- `bookStockList` は `book_stock.id` 昇順

### BOOK-014 在庫 JOIN で検索件数が増えない

事前条件:

- 検索対象 book のうち 1 件に複数店舗の在庫を登録する。
- 同じ検索条件について、在庫追加前の対象 book 件数を記録する。

Request:

```http
GET /api/books/search?page=0
```

HTTP / response 期待結果:

- HTTP 200
- `totalElements` は書籍件数と一致し、在庫行数を加算しない
- `content` に同じ book ID が重複しない
- 複数在庫を持つ book の `bookStockList` には全在庫が含まれる

### BOOK-015 空 keyword と最終ページ

手順:

1. `GET /api/books/search?page=0` を実行する。
2. `GET /api/books/search?keyword=%20%20&page=0` を実行する。
3. 初期データ 21 件、page size 10 の状態で `GET /api/books/search?page=2` を実行する。

HTTP / response 期待結果:

- 手順 1 と 2 の `content`、`totalElements`、`totalPages` が一致する
- 手順 3 は HTTP 200
- 手順 3 の `page` は 2、`size` は 10、`totalElements` は 21、`totalPages` は 3
- 手順 3 の `content` は 1 件

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

### BOOK-W-011 書籍登録の境界値

事前条件:

- 未使用 ISBN を 2 件用意する。

手順:

1. title 1 文字、author 0 文字、salesUnitPrice 1 で書籍を登録する。
2. title 100 文字、author 200 文字、salesUnitPrice 10000 で書籍を登録する。
3. title 101 文字、author 201 文字、salesUnitPrice 0 でそれぞれ登録を試みる。
4. salesUnitPrice 10001 で登録を試みる。

HTTP / response 期待結果:

- 手順 1 と 2 は HTTP 200 で、入力した境界値が返る
- 手順 3 と 4 は HTTP 400
- エラーの `errors[].field` に違反した `title`、`author`、`salesUnitPrice` を含む

DB 検証:

- 成功した 2 件だけ book と初期販売単価履歴が登録される
- 失敗した request に対応する book と履歴は存在しない

### BOOK-W-012 書籍更新の外部キー参照先なし

事前条件:

- テスト用 book を作成し、更新前の DB snapshot を取得する。

Request:

```http
POST /api/books/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "id": "<createdBookId>",
  "title": "参照先なし更新",
  "releaseDate": "2026-02-01",
  "publisherId": 1,
  "genreId": 999,
  "isbn": "<createdBookIsbn>",
  "version": "<createdBookVersion>"
}
```

HTTP / response 期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `book_genre(id=999)` を含む

DB 検証:

- book の全列と販売単価履歴が更新前 snapshot と一致する

### BOOK-W-013 書籍更新の ISBN 重複

事前条件:

- 更新対象 book と、別 ISBN を持つ既存 book を用意する。

Request:

```http
POST /api/books/update
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "id": "<createdBookId>",
  "title": "ISBN重複更新",
  "releaseDate": "2026-02-01",
  "publisherId": 1,
  "genreId": 5,
  "isbn": "0000000000001",
  "version": "<createdBookVersion>"
}
```

HTTP / response 期待結果:

- HTTP 400
- `title` が `データバリデーション`
- `detail` に `book(isbn=0000000000001)` を含む

DB 検証:

- 更新対象 book の ISBN、version、update_at は変更されない

### BOOK-W-014 ISBN 維持更新と販売単価の不変性

事前条件:

- salesUnitPrice 1200 のテスト用 book を作成する。
- 作成直後の book と販売単価履歴を記録する。

手順:

1. ISBN を変更せず、title と author だけを更新する。
2. 更新後に `GET /api/books/<createdBookId>` を実行する。

HTTP / response 期待結果:

- 更新と取得は HTTP 200
- ISBN は作成時の値のまま
- `salesUnitPrice` は 1200 のまま
- version は作成時 version + 1

DB 検証:

- book の title、author、update_at、version だけが更新内容に応じて変わる
- `book_sales_unit_price_history` の件数、単価、有効期間、version は変わらない

### BOOK-W-015 初期販売単価履歴登録失敗時のロールバック

事前条件:

- `BookSalesUnitPriceHistoryCustomDao.insertWithId` 相当を失敗させる failpoint を設定する。
- book と価格履歴の件数を記録する。

手順:

1. 未使用 ISBN で `POST /api/books/create` を実行する。

HTTP / response 期待結果:

- HTTP 5xx
- 成功 response を返さない

DB 検証:

- book 件数と価格履歴件数が実行前と一致する
- request の ISBN を持つ book が残らない

後処理:

- failpoint を解除する。

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

### PRICE-007 販売単価の境界値

事前条件:

- 販売単価 1200 のテスト用 book を 2 件作成する。

手順:

1. 1 件目へ salesUnitPrice 1、effectiveFrom `<futureDate1>` の履歴を追加する。
2. 2 件目へ salesUnitPrice 10000、effectiveFrom `<futureDate1>` の履歴を追加する。
3. 別 request で salesUnitPrice 0 を指定する。
4. 別 request で salesUnitPrice 10001 を指定する。

HTTP / response 期待結果:

- 手順 1 と 2 は HTTP 200
- 手順 3 と 4 は HTTP 400
- 手順 3 と 4 の `errors[].field` に `salesUnitPrice` を含む

DB 検証:

- 成功した book にだけ 1 または 10000 の履歴が追加される
- 0 または 10001 の履歴は登録されない

### PRICE-008 effectiveFrom の当日拒否と翌日許可

事前条件:

- Clock と DB の現在日を `<today>` に固定する。
- 同条件のテスト用 book を 2 件作成する。

手順:

1. effectiveFrom `<today>` で履歴追加を実行する。
2. effectiveFrom `<today> + 1 日` で履歴追加を実行する。

HTTP / response 期待結果:

- 手順 1 は HTTP 400 で、`errors[].field` に `effectiveFrom` を含む
- 手順 2 は HTTP 200

DB 検証:

- 当日開始の履歴は登録されない
- 翌日開始の履歴が登録され、初期履歴の effective_to は `<today>`

### PRICE-009 履歴期間の連続性と現在単価の切替

事前条件:

- `<today>` より前に開始した単価 1200 の履歴を持つ book を用意する。
- `<futureDate1>` に単価 1500、`<futureDate2>` に単価 1800 の履歴を API で追加する。

DB 検証:

- 1200 の effective_to は `<futureDate1> - 1 日`
- 1500 の effective_from は `<futureDate1>`、effective_to は `<futureDate2> - 1 日`
- 1800 の effective_from は `<futureDate2>`、effective_to は null
- 同じ book の期間に重複日と空白日がない
- `(book_id, effective_from)` はすべて一意

手順と HTTP / response 期待結果:

1. 基準日 `<futureDate1> - 1 日` の取得結果は salesUnitPrice 1200。
2. 基準日 `<futureDate1>` の取得結果は salesUnitPrice 1500。
3. 基準日 `<futureDate2> - 1 日` の取得結果は salesUnitPrice 1500。
4. 基準日 `<futureDate2>` の取得結果は salesUnitPrice 1800。
5. すべての取得は HTTP 200。

後処理:

- Clock と DB の基準日を戻す。

### PRICE-010 新履歴登録失敗時の期間更新ロールバック

事前条件:

- テスト用 book の価格履歴 snapshot を取得する。
- 直前履歴 update の後、`insertWithId` 相当で失敗する failpoint を設定する。

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

HTTP / response 期待結果:

- HTTP 5xx
- 成功 response を返さない

DB 検証:

- 直前履歴を含む全履歴が実行前 snapshot と一致する
- 1500 の新履歴が存在しない

後処理:

- failpoint を解除する。

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

### PURCHASE-011 details が空配列

Request:

```http
POST /api/purchases/create
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "purchaseInvoiceDate": "2026-02-01",
  "supplierId": 1,
  "receivingStoreId": 2,
  "details": []
}
```

HTTP / response 期待結果:

- HTTP 400
- `title` が `リクエストバリデーションエラー`
- `errors[].field` に `details` を含む

DB 検証:

- 仕入関連テーブルと在庫関連テーブルは変更されない

### PURCHASE-012 details が上限ちょうど 10 件

事前条件:

- 初期データに存在する ISBN を使った有効な明細を 10 件用意する。
- 各明細の単価を 100、数量を 1 とする。

Request:

- `POST /api/purchases/create` に上記 10 明細を指定する。

HTTP / response 期待結果:

- HTTP 200
- response の `detail` が入力順の 10 件
- `purchaseInvoiceAmount` が 1000
- 各明細の `purchaseInvoiceDetailAmount` が 100

DB 検証:

- `purchase_invoice` が 1 件追加される
- 同じ伝票 ID の `purchase_invoice_detail` が 10 件追加される
- `book_stock_movement` が 10 件追加される
- 対象在庫の数量増分合計が 10

### PURCHASE-013 同一 ISBN を複数明細で仕入れる

事前条件:

- store 2 / ISBN `0000000000001` の在庫数量と version を記録する。

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
      "purchaseInvoiceDetailIsbn": "0000000000001",
      "purchaseInvoiceDetailUnitPrice": 800,
      "purchaseInvoiceDetailQuantity": 3
    }
  ]
}
```

HTTP / response 期待結果:

- HTTP 200
- `purchaseInvoiceAmount` が 4400
- 明細金額が入力順に 2000、2400
- 両明細の `purchaseInvoiceDetailBookId` が同じ book ID

DB 検証:

- 対象在庫数量が合計 5 増える
- 対象在庫 version が 2 回更新された値になる
- 数量増分 2 と 3 の movement が入力順に 2 件登録される

### PURCHASE-014 伝票・明細・movement・在庫の関連整合性

事前条件:

- 既存在庫を持つ book と在庫を持たない book を 1 件ずつ明細に指定する。

HTTP / response 期待結果:

- HTTP 200
- 伝票 ID、明細 ID、updateAt、version は型と存在条件を満たす
- response の伝票金額は全明細金額の合計

DB 検証:

- 全明細の `purchase_invoice_id` が response の伝票 ID と一致する
- 明細の book ID、単価、数量、金額が request と response に一致する
- 各 movement の `source_id` が伝票 ID、`source_detail_id` が対応明細 ID
- movement の store ID、book ID、movement date、quantity delta が対応データと一致する
- `movement_type` は `PURCHASE`、`source_type` は `PURCHASE_INVOICE`
- 既存在庫は数量が加算され version が 1 増える
- 新規在庫は明細数量を初期数量として version 1 で作成される

### PURCHASE-015 明細途中失敗時の全体ロールバック

事前条件:

- 2 明細の有効な request を用意する。
- 伝票、明細、movement、対象在庫の snapshot を取得する。
- 2 件目の在庫 insert または update で失敗する failpoint を設定する。

手順:

1. `POST /api/purchases/create` を実行する。

HTTP / response 期待結果:

- HTTP 5xx
- 成功 response を返さない

DB 検証:

- `purchase_invoice`、`purchase_invoice_detail`、`book_stock_movement` の件数と内容が snapshot と一致する
- 1 件目を含む全対象 `book_stock` の数量、update_at、version が snapshot と一致する
- request に対応する伝票、明細、movement が残らない

後処理:

- failpoint を解除する。

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

### OPENBD-010 複数書誌の順序と内部 DTO 変換

OpenBD stub:

- ISBN A、B の順で受け取り、summary、onix、hanmoto を持つ書誌 A、B を同じ順で返す。
- 外部生成 DTO 固有の補助プロパティがある場合は、それにも値を設定する。

Request:

```http
GET /api/books/openbd?isbn=<isbnA>,<isbnB>
```

HTTP / response 期待結果:

- HTTP 200
- `Content-Type` は `application/json`
- response は A、B の順の 2 件
- 各要素の summary.isbn、summary.title、onix.RecordReference、hanmoto の主要項目が対応する stub 値と一致する
- response の構造は `OpenBdBookResponse` とその内部 response schema に適合する
- 外部 client の通信設定、HTTP response wrapper、例外情報など、内部 API schema にない生成 client 固有情報を含まない
- stub の呼び出しは 1 回で、ISBN query は request のカンマ区切り文字列と一致する

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

### ERROR-006 Problem Detail の content type と共通項目

手順:

1. request body validation error を発生させる。
2. データなしを発生させる。
3. 更新競合を発生させる。

HTTP / response 期待結果:

- すべての `Content-Type` が `application/problem+json`。charset parameter は許容する
- body.status が実際の HTTP status と一致する
- body.title が空でない
- body.detail を仕様で定義しているエラーは、その定義値と一致する
- body.instance が存在する場合は実際の request path と一致する
- validation error の `errors` は配列で、各要素に空でない `field` と `message` がある
- validation 以外のエラーで不要な `errors` を返さない

### ERROR-007 不正な JSON request body

Request:

```http
POST /api/auth/login
Content-Type: application/json

{"username":"admin","password":
```

HTTP / response 期待結果:

- HTTP 400
- `Content-Type` は `application/problem+json`
- body.status は 400
- body.title は `Bad Request` または同等の JSON 解析エラー分類
- 資格情報認証と token 発行は実行されない

### ERROR-008 行ロック競合と部分更新なし

事前条件:

- 別 DB 接続の明示トランザクションで対象 book 行を `FOR UPDATE` し、ロックを保持する。
- 対象 book と関連価格履歴の snapshot を取得する。

Request:

- 正しい version を指定して対象 book の更新 API を実行する。

HTTP / response 期待結果:

- 設定された限定回数のリトライ後に HTTP 409
- `title` が `更新競合`
- `detail` が `他ユーザーによって更新されています`

DB 検証:

- book と価格履歴が実行前 snapshot と一致する
- update_at と version も変更されない

後処理:

- ロック保持トランザクションをロールバックし、接続を閉じる。

## 10. カバレッジとトレーサビリティ

### 10.1 API operation 対応

| OpenAPI operation | 正常系 | 入力・業務異常 | 認証・共通エラー |
| --- | --- | --- | --- |
| `login` | `AUTH-001` | `AUTH-002`、`AUTH-003`、`AUTH-006` | `ERROR-007` |
| `resetLoginRateLimit` | `AUTH-007`、`AUTH-008` | なし | `AUTH-005`、`AUTH-009` から `AUTH-011` |
| `getBook` | `BOOK-001`、`BOOK-012`、`BOOK-013` | `BOOK-002` | `AUTH-004`、`ERROR-003` |
| `getBookSearch` | `BOOK-003` から `BOOK-008`、`BOOK-014`、`BOOK-015` | `BOOK-009` から `BOOK-011` | `AUTH-004`、`ERROR-002` |
| `createBook` | `BOOK-W-001`、`BOOK-W-011` | `BOOK-W-002` から `BOOK-W-005`、`BOOK-W-015` | `AUTH-005`、`ERROR-001`、`ERROR-004` |
| `updateBook` | `BOOK-W-006`、`BOOK-W-014` | `BOOK-W-007`、`BOOK-W-008`、`BOOK-W-012`、`BOOK-W-013` | `AUTH-005`、`ERROR-005`、`ERROR-008` |
| `deleteBook` | `BOOK-W-009` | `BOOK-W-010` | `AUTH-005`、`ERROR-003` |
| `createSalesUnitPrice` | `PRICE-001`、`PRICE-006` から `PRICE-009` | `PRICE-002`、`PRICE-004`、`PRICE-005`、`PRICE-010` | `PRICE-003`、`ERROR-008` |
| `createPurchaseInvoice` | `PURCHASE-001`、`PURCHASE-002`、`PURCHASE-012` から `PURCHASE-014` | `PURCHASE-003` から `PURCHASE-009`、`PURCHASE-011`、`PURCHASE-015` | `PURCHASE-010`、`ERROR-001` |
| `getBooksByIsbn` | `OPENBD-001`、`OPENBD-002`、`OPENBD-010` | `OPENBD-003` から `OPENBD-009` | `AUTH-004` |

### 10.2 Doma Service / DAO 分岐対応

| 設計上の分岐・不変条件 | ケース ID |
| --- | --- |
| 集約書籍が 0 件 / 在庫 0 件 / 在庫複数件 | `BOOK-002`、`BOOK-012`、`BOOK-013` |
| 書籍をページングしてから在庫 JOIN、count は在庫非依存 | `BOOK-007`、`BOOK-008`、`BOOK-014`、`BOOK-015` |
| 出版社・ジャンル参照、ISBN 一意性 | `BOOK-W-004`、`BOOK-W-005`、`BOOK-W-012`、`BOOK-W-013` |
| book insert と初期価格履歴 insert の同一トランザクション | `BOOK-W-001`、`BOOK-W-015` |
| book の悲観ロックと version 競合 | `BOOK-W-008`、`BOOK-W-014`、`ERROR-008` |
| 価格履歴の前履歴、後続履歴、同日重複 | `PRICE-004` から `PRICE-006`、`PRICE-009` |
| 価格履歴の update と insert の同一トランザクション | `PRICE-010` |
| 在庫 0 件なら insert、存在すれば update | `PURCHASE-001`、`PURCHASE-002`、`PURCHASE-014` |
| 同一 ISBN 複数明細を入力順に累積 | `PURCHASE-013` |
| 伝票、明細、movement、在庫の同一トランザクション | `PURCHASE-001`、`PURCHASE-014`、`PURCHASE-015` |
| DAO のロック・楽観ロック例外を更新競合へ変換 | `ERROR-005`、`ERROR-008` |

### 10.3 ケース数

| 分類 | ケース範囲 | 件数 |
| --- | --- | --- |
| 認証・認可 | `AUTH-001` から `AUTH-011` | 11 |
| 書籍取得・検索 | `BOOK-001` から `BOOK-015` | 15 |
| 書籍登録・更新・削除 | `BOOK-W-001` から `BOOK-W-015` | 15 |
| 販売単価履歴 | `PRICE-001` から `PRICE-010` | 10 |
| 仕入伝票 | `PURCHASE-001` から `PURCHASE-015` | 15 |
| OpenBD | `OPENBD-001` から `OPENBD-010` | 10 |
| 共通エラー | `ERROR-001` から `ERROR-008` | 8 |
| 合計 |  | 84 |

## 11. 実装完了判定

移植実装は、以下を満たしたら受け入れ可能とする。

- 本書のテストケースがすべて成功する。
- OpenBD 系テストは外部ネットワークに依存せず、モックまたはスタブで成功する。
- テストは DB 初期化後に何度でも再実行できる。
- 全 84 ケースが単独実行、任意順実行、連続 2 回実行で成功する。
- 書き込み系 API のテストは、作成データや DB 更新結果を検証している。
- 書き込み失敗と競合のテストは、関連テーブルに部分更新が残らないことを検証している。
- 認証必須 API と公開 API の境界がテストされている。
- validation、データなし、データバリデーション、認証、更新競合、外部 API 失敗のエラー分類がテストされている。
- OpenAPI operation と Doma Service / DAO の主要分岐が 10.1、10.2 のケースへ対応している。
