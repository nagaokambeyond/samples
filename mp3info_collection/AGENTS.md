# リポジトリ運用ガイド

## 概要

MP3のメタデータを再帰的に読み取り、SQLiteへ保存するKotlin/JVM製CLIです。データベースアクセスにはDoma 3、全文検索にはSQLite FTS5を使用します。

## ビルドとテスト

- 全テストは `./gradlew test` で実行します。
- CLIは `./gradlew run --args="<command> ..."` で実行します。
- 利用可能なコマンドは `scan <source-directory> <sqlite-db-path>`、`search <sqlite-db-path> <query>`、`clear <sqlite-db-path>` です。

## データベースの規約

- SQLiteの読取り・書込み・スキーマ変更・保守処理は、すべてDomaのDAOメソッドおよびSQL／スクリプトリソースを経由させます。場当たり的なJDBC文は追加しません。
- DAOのSQLリソースは `src/main/resources/META-INF/com/example/mp3info/persistence/TrackDao/` 配下に置き、DAOメソッド名と一致させます。
- `tracks.path` は不変の主キーです。再スキャン時に重複行を作らず更新されるよう、UPSERTの挙動を維持します。
- `tracks_fts` は `tracks` をコンテンツとする外部コンテンツFTS5インデックスです。`tracks` を変更する経路を追加する場合、FTSインデックスとの整合性も維持します。スキャン後にはインデックスを再構築し、クリア時には両テーブルを空にします。
- FTS導入前のDBで初回のFTS再構築が一度だけ実行されるよう、`schema_migrations` の移行記録を維持します。

## テスト方針

- DAO、スキーマ、検索、インデックスを変更する場合は、一時SQLiteデータベースを使う統合テストを追加または更新します。
- スキャナーのテストでは `Mp3MetadataReader` インターフェースを使い、実MP3ファイルのフィクスチャに依存させません。
- 永続化または検索のコードを変更する場合は、通常動作に加えて再スキャン時・クリア時の挙動も検証します。
