/*
 * Created on 2004/10/10
 *
 */
package com.nullfish.app.jfd2.aliase;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.util.FileHistory;
import com.nullfish.lib.vfs.FileSystem;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * エイリアス情報
 * 
 * @author shunji
 */
public class AliaseInfo {
	/**
	 * エイリアス先ファイル
	 */
	private String path;
	
	/**
	 * trueなら同一ファイルシステムの最後に開かれたディレクトリを指し示す。
	 */
	private boolean lastOpenedDir;
	
	/**
	 * 対象jFD
	 */
	private JFD jfd;
	
	/**
	 * コンストラクタ
	 * 
	 * @param path	ファイル
	 * @param lastOpenedDir	もしもtrueなら同一ファイルシステムの最後に開かれたディレクトリを指し示す。
	 * @param jfd	このオブジェクトを保持するjFD2インスタンス
	 */
	public AliaseInfo(String path, boolean lastOpenedDir, JFD jfd) {
		this.path = path;
		this.lastOpenedDir = lastOpenedDir;
		this.jfd = jfd;
	}
	
	/**
	 * エイリアス先ファイルを取得する。
	 * 
	 * @return
	 */
	public VFile getFile() {
		try {
			VFile file = VFS.getInstance(jfd).getFile(path);
			if(!lastOpenedDir) {
				return file;
			}
			
			FileSystem fileSystem = file.getFileSystem();
			FileHistory history = jfd.getModel().getHistory();
			for(int i=0; i<history.getSize(); i++) {
				VFile oldFile = history.fileAt(i);
				if(oldFile != null && oldFile.getFileSystem().equals(fileSystem)) {
					return oldFile;
				}
			}
			
			return file;
		} catch (VFSException e) {
			return null;
		}
	}
}
