/*
 * Created on 2004/05/18
 *
 */
package com.nullfish.app.jfd2;

/**
 * JFDModelの変更通知を受け取るリスナインターフェイス。
 * これらのメソッドは、必ずしもSwingのイベントディスパッチスレッドから
 * 投げられるとは限らないということに注意。
 * 実装クラスはスレッドセーフにする必要がある。
 * 
 * @author shunji
 */
public interface JFDModelListener {
	/**
	 * 表示中のファイルリストが変更された際に呼び出される。
	 * 
	 * @param model		モデル
	 */
	public void dataChanged(JFDModel model);
	
	/**
	 * ディレクトリが変更された際に呼び出される。
	 * 
	 * @param model	モデル
	 */
	public void directoryChanged(JFDModel model);
	
	/**
	 * カーソル移動時に呼び出される。
	 * @param index
	 */
	public void cursorMoved(JFDModel model);
	
	/**
	 * マーク状態が変化したときに呼び出される。
	 * @param model
	 */
	public void markChanged(JFDModel model);
}
