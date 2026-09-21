# MP3 Info Collection

指定フォルダ以下のMP3を再帰走査し、ID3タグと音声情報をSQLiteへ保存するKotlin CLIです。SQLiteへのアクセスにはDoma 3を使用します。

## 実行

```bash
./gradlew run --args="scan /path/to/music /path/to/mp3-library.sqlite"
```

同一の絶対パスを再スキャンすると、既存行を重複させず最新情報へ更新します。読み取れないファイルは標準エラーに出力し、残りの処理を続行します。

## 保存内容のクリア

SQLiteファイルとテーブル定義は残したまま、登録済みのトラック情報をすべて削除します。

```bash
./gradlew run --args="clear /path/to/mp3-library.sqlite"
```

## 検索

SQLite FTS5の全文検索インデックスを使用して、タイトル、アーティスト、アルバム、アルバムアーティスト、ジャンル、ファイルパスを検索します。スキャン完了時にインデックスを更新します。複数の単語を指定した場合は、すべての単語を含むトラックを返します。

```bash
./gradlew run --args="search /path/to/mp3-library.sqlite jazz"
```
