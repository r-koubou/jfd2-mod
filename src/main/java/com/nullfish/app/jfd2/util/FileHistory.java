/*
 * Created on 2004/06/28
 *
 */
package com.nullfish.app.jfd2.util;

import java.util.ArrayList;
import java.util.List;

import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public class FileHistory {
	private int position;
	
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
	public FileHistory(int maxSize, boolean noOverwraps) {
		this.maxSize = maxSize;
		this.noOverwraps = noOverwraps;
	}
	
	/**
	 * 
	 * @param file
	 */
	public void add(VFile file) {
		if(history.size() > 0 && file.equals(history.get(position))) {
			return;
		}
		
		for(int i=0; i<position; i++) {
			history.remove(0);
		}
		
		position = 0;
		
		if(noOverwraps) {
			history.remove(file);
		}
		
		history.add(0, file);
		if(history.size() > maxSize) {
			history.remove(history.size() - 1);
		}
	}
	
	/**
	 * 指定インデックス版目のファイルを取得する。
	 * @param index
	 * @return
	 */
	public VFile fileAt(int index) {
		return (VFile)history.get(index);
	}
	
	/**
	 * ヒストリのサイズを取得する。
	 * @return
	 */
	public int getSize() {
		return history.size();
	}

	/**
	 * 配列にして返す
	 * @return
	 */
	public VFile[] toArray() {
		VFile[] rtn = new VFile[history.size()];
		rtn = (VFile[])history.toArray(rtn);
		return rtn;
	}
	
	/**
	 * リストにして返す
	 * @return
	 */
	public List toList() {
		return new ArrayList(history);
	}
	
	/**
	 * 一つ前のヒストリにカーソルを合わせ、そのファイルを返す。
	 * 
	 * @return
	 */
	public VFile prev() {
		position++;
		while(position >= history.size()) {
			position--;
		}
		
		return (VFile)history.get(position);
	}
	
	/**
	 * 一つ次のヒストリにカーソルを合わせ、そのファイルを返す。
	 * 
	 * @return
	 */
	public VFile next() {
		position--;
		while(position < 0) {
			position++;
		}
		
		return history.size() == 0 ? null : (VFile)history.get(position);
	}
}
