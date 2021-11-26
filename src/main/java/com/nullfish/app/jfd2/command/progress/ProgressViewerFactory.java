/*
 * Created on 2004/08/29
 *
 */
package com.nullfish.app.jfd2.command.progress;

/**
 * 作業経過表示画面のファクトリーインターフェイス
 * 
 * @author shunji
 */
public interface ProgressViewerFactory {
	/**
	 * 経過表示画面を取得する。
	 * @return
	 */
	public ProgressViewer getProgressViewer();
}
