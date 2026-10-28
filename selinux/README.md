# SELinux QEMU ラボ

Fedora Cloud を QEMU で起動し、意図的な SELinux 拒否（AVC）を発生させて原因を調べるための最小環境です。ホストの SELinux 設定や Docker デーモンには変更を加えません。

## 前提条件

- QEMU 8 以降（Linux では KVM を使えると高速）
- `qemu-img`
- cloud-init の seed ISO を作るコマンド: `genisoimage`、`mkisofs`、または macOS の `hdiutil`
- Fedora のアーキテクチャに合った **Cloud qcow2 image** をダウンロード済みであること

macOS では `brew install qemu` で QEMU を導入できます。Apple Silicon では `aarch64` の Fedora Cloud image と、QEMU/UTM が提供する AArch64 UEFI firmware が必要です。x86_64 image は動作しますがエミュレーションになるため遅くなります。

## 初回セットアップ

1. Fedora Project の Cloud downloads から、自分の CPU に合う Generic Cloud qcow2 image を取得します。
   - Intel/AMD: `x86_64`
   - Apple Silicon: `aarch64`
2. ダウンロードしたファイルを指定して、書き込み用ディスクと cloud-init の seed ISO を作ります。

   ```sh
   ./scripts/prepare-image.sh ./Fedora-Cloud-Base-Generic-44-1.7.aarch64.qcow2
   ```

3. VM を起動します。既定メモリは 2 GB です。必要に応じて、たとえば `MEMORY=4096 ./scripts/run-qemu.sh` のように増やせます。

   ```sh
   ./scripts/run-qemu.sh
   ```

   Apple Silicon で UEFI firmware を自動検出できない場合は、QEMU に付属する firmware のパスを明示します。

   ```sh
   EFI_CODE=/opt/homebrew/opt/qemu/share/qemu/edk2-aarch64-code.fd \
  ./scripts/run-qemu.sh
   ```

4. 起動後、別の端末からログインします。

   ```sh
   ssh -p 2222 selinux@localhost
   ```

   パスワードは `selinux` です。これはローカル学習用だけの値です。ポートは localhost にだけ公開されます。

5. ログインできない場合、古い鍵を削除する

```
selinux % ssh -p 2222 selinux@localhost
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@    WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!     @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!
Someone could be eavesdropping on you right now (man-in-the-middle attack)!
It is also possible that a host key has just been changed.
The fingerprint for the ED25519 key sent by the remote host is
SHA256:O7a40ShAA0Q4S6rHJ26vtbpkdynVscPYv3k6+5JonkU.
Please contact your system administrator.
Add correct host key in /Users/*****/.ssh/known_hosts to get rid of this message.
Offending ECDSA key in /Users/*****/.ssh/known_hosts:6
Host key for [localhost]:2222 has changed and you have requested strict checking.
Host key verification failed.
selinux % ssh-keygen -R '[localhost]:2222'
# Host [localhost]:2222 found: line 4
# Host [localhost]:2222 found: line 5
# Host [localhost]:2222 found: line 6
/Users/*****/.ssh/known_hosts updated.
Original contents retained as /Users/*****/.ssh/known_hosts.old
```

cloud-init が終わるまで一、二分待ち、`cloud-init status --wait` が成功することを確認してください。初回セットアップ中に、サンプル用の最小ポリシーと実行ファイルがインストールされます。

## 最初の拒否を起こして解析する

ゲストで以下を実行します。

```sh
sudo /opt/selinux-lab/deny-demo.sh
sudo /opt/selinux-lab/inspect-denial.sh
```

`deny-demo.sh` は `selinux_lab_t` ドメインに遷移する小さなプログラムを実行します。このドメインには `/srv/selinux-lab/secret.txt` の**検索**権限だけを与え、ファイルを開く・読む権限は意図的に与えていません。したがってプログラムは `Permission denied` で終了し、audit log に AVC が記録されます。

`inspect-denial.sh` は、enforcing 状態・ファイルラベル・直近の AVC・`audit2why` による拒否理由・`sesearch` による許可ルールの確認を表示します。

解析結果を理解したら、`/opt/selinux-lab/selinux_lab.te` に最小限の許可を追加し、ゲスト上でモジュールを再ビルドして挙動が変わることを試せます。学習用なので、`audit2allow` の出力を無条件で導入しないでください。

```sh
cd /opt/selinux-lab
sudo make -f /usr/share/selinux/devel/Makefile selinux_lab.pp
sudo semodule -i selinux_lab.pp
sudo restorecon -Rv /usr/local/bin/selinux-lab-reader /srv/selinux-lab
sudo /opt/selinux-lab/deny-demo.sh
```

## 停止と作り直し

QEMU の画面で `Ctrl-a` のあと `x` を押すと停止します。ゲスト内では `sudo poweroff` でも停止できます。

`lab/fedora.qcow2` が実験用の差分ディスクです。まっさらな状態に戻すには、VM を停止してから `scripts/prepare-image.sh` をもう一度実行します。ベース image は変更されません。

## ファイル構成

- `scripts/prepare-image.sh`: base qcow2 から差分ディスクと NoCloud seed ISO を作成
- `scripts/run-qemu.sh`: CPU アーキテクチャに応じて QEMU を起動
- `cloud-init/`: 初回起動時に実行される設定
