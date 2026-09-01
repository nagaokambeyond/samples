# 永続化実装の共通方針

この文書は JPA / MyBatis / Doma / jOOQ に共通する方針を扱います。方式固有の変更では、対応する方式別文書と `schema-and-codegen.md` も確認してください。

## Service・validator・converter

- `BooksOperationService` と `PurchaseOperationService` は4方式共通の Service インターフェースです。変更時は4実装を揃えます。
- `BookDataValidator*` は出版社・ジャンルの参照存在確認と ISBN 一意性確認を扱います。
- `PurchaseDataValidator*` は仕入先・受入店舗・明細 ISBN の参照存在確認と、ISBN から本 ID への解決を扱います。
- `BookOperationConverter*` は projection / 表示向け Entity / row から `BookResponse`、`BookPageResponse` への変換を扱います。
- `PurchaseOperationConverter*` は仕入登録用 Entity / row、明細・伝票金額、在庫、在庫増減履歴、response DTO の変換を扱います。
- DB を読む・更新する Service メソッドには `@Transactional` を付けます。

現在のデフォルト profile は `doma` で、`BooksOperationServiceDoma` と `PurchaseOperationServiceDoma` には `@Primary` が付いています。profile や Bean 優先順位を変える場合は `application.yaml`、各実装の `@Profile`、Doma の `@Primary` を確認します。

## 書籍の取得・検索

- レスポンスには出版社名、ジャンル名、ISBN、現在販売単価、店舗別在庫を含めます。
- 現在販売単価は `effective_from <= current_date` かつ `effective_to IS NULL OR current_date <= effective_to` の履歴です。
- 取得 query と検索 query、検索 count は、現在単価を含む検索条件を揃えます。
- 在庫・店舗を結合する一覧検索は、先に書籍を `limit` / `offset` でページングしてから在庫行を結合し、行重複でページサイズが崩れないようにします。
- `keyword` が未指定または空文字の場合はタイトル・著者条件を付けません。
- offset と totalPages は `PageCalculator` で計算し、方式ごとに式を重複させません。
- JPA と jOOQ の取得結果は在庫単位の複数行なので converter で書籍単位に集約します。MyBatis と Doma は表示向け Entity に在庫 collection を集約します。

## 登録・更新・ロック

- 本登録時は `BookCreateRequest.salesUnitPrice` と `releaseDate` から販売単価の初期履歴を作成します。
- 販売単価履歴追加では対象の本をロックし、前履歴と後続履歴の `effective_to` を調整します。
- 仕入登録では、伝票と明細を登録し、対象在庫をロックして新規作成または数量加算し、`book_stock_movement` に `PURCHASE` / `PURCHASE_INVOICE` を登録します。
- 排他ロックを使う Service メソッドは既存の `@RetryableOnLockFailure` 方針を維持します。
- 楽観・悲観ロック失敗は Spring の `ObjectOptimisticLockingFailureException` / `PessimisticLockingFailureException` に揃え、`GlobalExceptionHandler` で 409 として扱います。
- 更新・削除時のバージョンチェック、書き込みロック、ロック失敗リトライを不用意に変更しません。

## データ整合性

- `book.publisher_id`、`book.genre_id`、仕入先、受入店舗、仕入明細 ISBN の参照存在確認は各 validator に集約します。
- ISBN は13桁の一意キーです。登録・更新時の違反は `UniqueConstraintValidationException` として扱います。
- request DTO から採番対象 Entity / row を作る際は、外部キーが主キー `id` に暗黙マッピングされないよう登録項目を明示し、主キーは採番処理まで未設定にします。
- 共有ドメイン型の DB 表現は4方式で揃えます。詳細は `schema-and-codegen.md` を参照してください。

## 関連テスト

方式固有の `BooksOperationService*Test`、`PurchaseOperationService*Test`、`BookDataValidator*Test`、`PurchaseDataValidator*Test` を確認します。変更が共通仕様に関わる場合は4方式すべてを確認します。詳しい選択基準は `../testing-guide.md` を参照してください。
