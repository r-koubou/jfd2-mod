/*
 * Created on 2004/06/28
 *
 */
package com.nullfish.app.jfd2.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shunji
 *
 */
public class StringHistory {
	/**
	 * 最大記録数
	 */
	private int maxSize = 50;
	
	/**
	 * ヒストリ
	 */
	private List history = new ArrayList();
	
	/**
	 * 同一オブジェクトの保持を禁止するかのフラグ
	 */
	private boolean noOverwraps = false;
	
	/**
	 * コンストラクタ
	 * @param maxSize
	 */
	public StringHistory(int maxSize, boolean noOverwraps) {
		this.maxSize = maxSize;
		this.noOverwraps = noOverwraps;
	}
	
	/**
	 * 
	 * @param str
	 */
	public void add(String str) {
		if(noOverwraps) {
			history.remove(str);
		}
		
		history.add(0, str);
		if(history.size() > maxSize) {
			history.remove(history.size() - 1);
		}
	}
	
	/**
	 * 指定インデックス版目の文字列を取得する。
	 * @param index
	 * @return
	 */
	public String getAt(int index) {
		return (String)history.get(index);
	}
	
	/**
	 * ヒストリのサイズを取得する。
	 * @return
	 */
	public int getSize() {
		return history.size();
	}
	/**
	 * @return Returns the maxSize.
	 */
	public int getMaxSize() {
		return maxSize;
	}
	/**
	 * @return Returns the noOverwraps.
	 */
	public boolean isNoOverwraps() {
		return noOverwraps;
	}
	
	/**
	 * 履歴を配列として取得する
	 * @return
	 */
	public String[] toArray() {
		String[] rtn = new String[history.size()];
		rtn = (String[])history.toArray(rtn);
		return rtn;
	}
	
	/**
	 * 履歴をリストとして取得する
	 * @return
	 */
	public List toList() {
		return new ArrayList(history);
	}
}
