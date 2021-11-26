/*
 * Created on 2004/09/12
 *
 */
package com.nullfish.app.jfd2.ext_command;

/**
 * <p>タイトル: StandardOutputProcessReader</p>
 * <p>説明: jFDの外部コマンドからの出力を標準出力に出力するリーダークラス</p>
 * <p>著作権: Copyright (c) Shunji Yamaura</p>
 * <p>会社名: </p>
 * @author Shunji Yamaura
 * @version 1.0
 */


public class StandardOutputProcessReader implements ProcessReader {
    public static final int READER_ID = 2;
    
    /**
     * このクラスのシングルトンインスタンス
     */
    private static StandardOutputProcessReader instance = new StandardOutputProcessReader();

    /**
     * デフォルトコンストラクタ
     */
    private StandardOutputProcessReader() {}

    /**
     * このクラスのシングルトンインスタンスを取得するメソッド
     */
    public static ProcessReader getInstance() {
        return instance;
    }

    public void addProcess(Process process) {
        StreamReaderThread outReaderThread = new StreamReaderThread(process.getInputStream(), System.out);
        outReaderThread.start();
        StreamReaderThread errReaderThread = new StreamReaderThread(process.getErrorStream(), System.err);
        errReaderThread.start();
    }

    /**
     * このクラスのIDを取得する
     */
    public int getReaderID() {
        return READER_ID;
    }

    /**
     * 表示する
     */
    public void showConsole(boolean visible) {
        //  何もしない
    }

	public void setShowsAutomatic(boolean shows) {
	}
}
