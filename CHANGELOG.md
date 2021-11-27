# CHANGELOG

## ver-1.01-mod2

### com.nullfish.app.jfd2.util.MacUtil クラス

代替のAPIコールに置き換えた

### com.nullfish.app.jfd2.Launcher クラス

システムプロパティのセットタイミングをmain直後に指定

- L&Fなど、AWT 初期化前に設定しないと反映されないため

## ver-1.01-mod1

http://openjdk.java.net/jeps/272 の回避

> In the forthcoming release of JDK 9, internal APIs such as those in the Mac OS X com.apple.eawt package will no longer be accessible.

- macOS 10.15 Catalinaから Apple Java VM のインストールができない
- Java  8 は 2019 年に EOL になっている
- jFD2が Java 9 から利用不可能になった Mac OS 固有の API を使用している

という理由から、Java 9 以降の環境で jFD2.app 内の jar ファイルを直接 java コマンドで実行したとしてもエラーになる。そこで、それに起因する起動エラーを回避するための対処を実施。

73bedcb9d88a9a966f8088b03418bd51eeed3997

  - `com.nullfish.app.jfd2.util.MacUtil` クラス
    - `com.apple.eawt` パッケージを使用している当該箇所のコメントアウト
    - タスクバーに対する振る舞い変更のみだったため通常使用には影響はない

### 起動方法

```bash
java -jar jfd2-1.01-mod1-jar-with-dependencies.jar
```
