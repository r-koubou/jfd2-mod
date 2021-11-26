/*
 * Created on 2004/05/23
 *
 */
package com.nullfish.lib.ui.list_table;

/**
 * 選択を保持するクラスのインターフェイス。
 * 
 * @author shunji
 */
public interface ListCursorModel {
	/**
	 * 選択インデックスを返す。
	 * @return
	 */
	public int getSelectedIndex();
	
	/**
	 * 選択インデックスを設定する。。
	 * @param index インデックス
	 */
	public void setSelectedIndex(int index);
	
	/**
	 * モデルのリスナをセットする。
	 * @param listener
	 */
	public void addListCursorModelListener(ListCursorModelListener listener); 
}
