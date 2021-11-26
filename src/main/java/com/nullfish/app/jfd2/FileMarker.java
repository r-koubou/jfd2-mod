/*
 * Created on 2004/06/06
 *
 */
package com.nullfish.app.jfd2;

import com.nullfish.lib.vfs.VFile;

/**
 * VFileとマーク状態をまとめて扱うためのクラス
 * 
 * @author shunji
 */
public class FileMarker {
	private boolean marked;
	
	private VFile file;
	
	public FileMarker(VFile file) {
		this.file = file;
	}
	
	/**
	 * @return Returns the file.
	 */
	public VFile getFile() {
		return file;
	}
	
	/**
	 * @return Returns the marked.
	 */
	public boolean isMarked() {
		return marked;
	}
	
	public void setMarked(boolean marked) {
		this.marked = marked;
	}
}
