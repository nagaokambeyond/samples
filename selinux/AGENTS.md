# 開発ガイド

このリポジトリは、QEMU 上で Fedora を起動し、SELinux の拒否と解析を学ぶためのローカル実験環境です。

## 構成

- `scripts/prepare-image.sh`: Fedora Cloud image を変更せず、差分 qcow2 と cloud-init 用 seed ISO を生成する。
- `scripts/run-qemu.sh`: QEMU を起動する。SSH は localhost のポート 2222 にだけ転送する。
- `cloud-init/user-data`: ゲスト初回起動時の設定、サンプルポリシー、再現・解析スクリプトを定義する。
- `lab/`: ローカルで生成されるディスク・ISO の置き場。生成物は Git に追加しない。

## 変更時の方針

- ベースの Fedora Cloud image を直接書き換えない。実験結果は必ず `lab/fedora.qcow2` の差分ディスクに保存する。
- SELinux の拒否サンプルでは、拒否を安易に無効化しない。`setenforce 0`、広すぎる `allow` ルール、無検討の `audit2allow` 出力の導入を解決策にしない。
- ポリシーを変更する場合は、必要最小限の型・オブジェクトクラス・権限だけを追加する。なぜ必要かをポリシー内のコメントか README に残す。
- 外部へ公開するポートを追加しない。ネットワーク転送は `127.0.0.1` に限定する。
- 学習用の SSH パスワードは README の用途に限る。本番環境向けの認証設定として扱わない。
- Apple Silicon を含む複数アーキテクチャでの利用を意識し、ホスト固有のパスや firmware を固定値にしない。

## 検証

スクリプトを変更したら、少なくとも次を実行する。

```sh
bash -n scripts/prepare-image.sh scripts/run-qemu.sh
git diff --check
```

cloud-init を変更したら、YAML として読み込めることを確認する。

```sh
ruby -e 'require "yaml"; YAML.load_file("cloud-init/user-data"); YAML.load_file("cloud-init/meta-data")'
```

`qemu-img` が利用できる場合は、一時的な qcow2 を使って `scripts/prepare-image.sh` が差分ディスクと seed ISO を生成できることも確認する。実際の Fedora image のダウンロードや VM の長時間起動は、明示的に依頼された場合だけ行う。

## ドキュメント

- 実行方法・必要な依存関係・既定値を変えた場合は `README.md` も更新する。
- コマンド例はコピーして実行できる状態に保つ。
- セキュリティ上意図的に弱くしている箇所は、その目的と利用範囲を明記する。
