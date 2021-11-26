jFD2　beta19

●概要

jFD2はJavaで記述されたキーボード操作のファイル管理ソフトです。拙作jFDの次期バージョンとして一から作り直され、以下のような特徴を持ちます。

・FDシリーズに準じた操作性
・仮想ファイルシステムによるネットワーク、アーカイブ親和性
・スクリプト実行機能による拡張性
・柔軟なレイアウト機能（タブ、画面分割）
・強力な検索機能（サブフォルダも含んだファイル名検索、Grep検索）
・強力なカスタマイズ機能
・特定のファイルや、ファイル抽出条件が登録可能な仮想ディレクトリ機能
・ユーザー管理可能なファイルのタグ付け機能

※jFDについて
jFDはMS－DOS用のFD（出射厚氏作）、Windows用のWinFD（高橋直人氏作）の操作感覚を再現しつつ機能を強化し、さらにそれを全てのプラットホーム上でサポートすることを目標に制作されました。

●動作環境

JavaSE 5 以上。ただし、ローカルディスクの容量、空き容量の表示を行うにはJava SE 6以上が必要です。
LinuxではJavaのバグのため、現在の最新バージョンのJava以外はサポート対象外とします。
本バージョンはWindows XP、MacOSX10.4以降、Ubuntu Linux8.10をサポート対象とします（他プラットフォームは所持していないので検証されていません。おそらく動きます）
※グラフィックビューアでBMP、TIFF、JPEG2000を表示するにはJava Advanced Imaging Image I/O Toolsのインストールが必要です。こちらからダウンロードしてください。
http://java.sun.com/products/java-media/jai/downloads/download-iio-1_0_01.html

●推奨環境

・最新のJava SE 6
・1GHz以上のCPU
・512MB以上のメモリー
・Windows XP以上、MacOSX 10.5以上、Ubuntu 8.10以上

●実行方法

・Windowsの場合
まず、Java6以上がインストールされていない場合、インストールする必要があります。下記サイトでJREを入手し、インストールしてください。

http://java.com/ja/download/download_the_latest.jsp

JREのインストールが完了したら、適当なディレクトリにこのソフトのアーカイブを解凍し、jfd2.exeをダブルクリックしてください。

・Macの場合
アーカイブを解凍し、jFD2.appを実行してください。

●アンインストール

jFD2をインストールしたディレクトリと、以下のディレクトリを削除してください。
・Windows2000, XPの場合
	C:\Documents and Settings\（ユーザー名）\Application Data\Nullfish
・Windows Vistaの場合
	C:\Users\（ユーザー名）\AppData\Roaming\Nullfish
・UNIX（Macも含まれます）
	ホームディレクトリの.jfd2と.jfd2_user


●使用方法

同梱された、keys.htmlを参照してください。

●制限事項

二つ以上のjFD2で同時にサムネイルモードを使用しないでください
（同一VM上の別ペインまたは別ウインドウでしたら問題ありません）。
最初にサムネイルモードに入ったjFD2が終了するまで、もう片方が固まります。

●Mac環境での既知の問題点
・MacではJava環境のバグのため、日本語キーボードを使用した際に、記号キーが間違ったキーとして認識されます。これに対処するために、キーの変換機能を作成しました。オプション画面の「その他」タブ内にあるキー変換ファイルに、同梱のkeymap_mac_jp.xmlのパスを指定して使用してください。

●謝辞

偉大なる先人であるDOS版FDの作者である出射厚氏、WinFDの作者である高橋直人氏に深く御礼を申し上げます。

また、ベータテスター兼、燃料購入担当兼、WinFD機能スパイ兼、元奴隷商人で今は病人の池田憲一氏にも深く感謝いたします。

本ソフトウェアのLHA操作にはDANGAN氏製作のjLHAを使用しています。
（http://homepage1.nifty.com/dangan/）

本ソフトウェアはK.Takata氏製作のFastFileを使用しています。
（http://homepage3.nifty.com/k-takata/mysoft/fastfile.html）

本ソフトウェアはnaomichi氏よりソースコードを提供していただきました。
（http://longinus.dyndns.org/mittan/）

tar, gzip, bzip2対応は、Lei氏よりソースコードを提供していただきました。

*This product includes software developed by the
 Apache Software Foundation (http://www.apache.org/).

*This products uses jcifs library(http://jcifs.samba.org/).

*本ソフトウェアのインクリメンタルサーチに、n|a氏製作のJ/Migemoを使用しています。（http://v2c.s50.xrea.com/jmigemo/）

●ライセンス

jFD2はApacheライセンス2.0の元で公開されています。 
Apacheライセンス2.0については、http://www.apache.org/licenses/LICENSE-2.0.htmlを参照してください。
本プログラムの著作権は山浦俊司にあります。
本プログラムの使用によって何らかの障害が発生した場合でも、作者は一切の責任を負わないものとします。


＊ご感想、ご要望などありましたら、是非メール、ブログ、掲示板などにお知らせください。
