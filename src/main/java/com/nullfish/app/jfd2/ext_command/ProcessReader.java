/*
 * Created on 2004/09/12
 *
 */
package com.nullfish.app.jfd2.ext_command;

/**
 * <p>タイトル: ProcessReader</p>
 * <p>説明: 外部コマンドからの出力を読みとるクラスのインターフェイス</p>
 * <p>著作権: Copyright (c) Shunji Yamaura</p>
 * <p>会社名: </p>
 * @author Shunji Yamaura
 * @version 1.0
 */


public interface ProcessReader {
    /**
     * このリーダークラスに出力するプロセスを追加する
     */
    public void addProcess(Process process);

    /**
     * このクラスのIDを取得する
     */
    public int getReaderID();

    /**
     * 表示する
     */
    public void showConsole(boolean visible);
    
    /**
     * 出力時に自動で表示するかどうかをセットする。
     * @param shows
     */
    public void setShowsAutomatic(boolean shows);
}
