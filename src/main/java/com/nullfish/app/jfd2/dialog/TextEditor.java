/*
 * Created on 2004/07/18
 *
 */
package com.nullfish.app.jfd2.dialog;

import javax.swing.JComponent;

/**
 * @author shunji
 *
 */
public interface TextEditor {
	/**
	 * 入力結果を取得する。
	 * @return
	 */
	public String getAnswer();
	
	/**
	 * 入力結果を設定する。
	 */
	public void setAnswer(String text);

	/**
	 * コンポーネントを取得する。
	 * @return
	 */
	public JComponent getComponent();
}
