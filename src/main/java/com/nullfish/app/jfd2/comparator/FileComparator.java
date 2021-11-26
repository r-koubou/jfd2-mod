/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.app.jfd2.comparator;

import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 *
 */
public interface FileComparator {
	/**
	 * ファイルを比較し、file1が大きければ負の数を、
	 * file2が大きければ正の数を、等しければ0を返す。
	 * @param file1
	 * @param file2
	 * @return
	 */
	public int compare(VFile file1, VFile file2);
}
