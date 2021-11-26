/*
 * Created on 2004/06/20
 *
 */
package com.nullfish.app.jfd2;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 特定のディレクトリのファイルを元に初期化可能なクラスのインターフェイス
 * 
 * @author shunji
 */
public interface Initable {
	public void init(VFile baseDir) throws VFSException;
}
