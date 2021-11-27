# CHANGELOG

## ver-1.01-mod1

macOS 10.15 Catalinaから Apple Java VM のインストールができず、jFD2.app 内の jar ファイルを
直接 java コマンドで実行したとしてもエラーになる。そこで、それに起因する起動エラーを回避するための対処を実施。

  - `com.nullfish.app.jfd2.util.MacUtil` クラス
    - `com.apple.eawt` パッケージを使用している当該箇所のコメントアウト
    - タスクバーに対する振る舞い変更のみだったため通常使用には影響はない

### 起動方法

```bash
java -jar jfd2-1.01-mod1-jar-with-dependencies.jar
```
