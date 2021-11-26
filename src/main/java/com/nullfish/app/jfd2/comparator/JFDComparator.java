/*
 * Created on 2004/05/23
 *
 */
package com.nullfish.app.jfd2.comparator;

import java.util.Comparator;

import com.nullfish.lib.vfs.VFile;

/**
 * @author shunji
 * 
 */
public class JFDComparator implements Comparator {
	/**
	 * ディレクトリ比較のフラグ
	 */
	private boolean comparesDirectory = false;

	/**
	 * ファイル比較クラスの配列。
	 * 優先度の高い順に入っている。
	 */
	private FileComparator[] comparators;
	
	public JFDComparator(FileComparator[] comparators) {
		this.comparators = comparators;
	}
	
	public int compare(Object o1, Object o2) {
		if(o1 == null && o2 == null) {
			return 0;
		}
		
		if(o1 == null) {
			return 1;
		}
		
		if(o2 == null) {
			return -1;
		}
		
		VFile file1 = (VFile)o1;
		VFile file2 = (VFile)o2;
		
		int rtn = 0;
		
		for(int i=0; i < comparators.length && rtn == 0; i++) {
			rtn = comparators[i].compare(file1, file2);
		}
		
		return rtn;
	}
	
	/**
	 * ファイルの親ディレクトリを名称で比較する。
	 */
	public int compareDirectory(VFile file1, VFile file2) {
		VFile parent1 = file1.getParent();
		VFile parent2 = file2.getParent();

		if (parent1 == null && parent2 == null) {
			return 0;
		}

		if (parent1 == null && parent2 != null) {
			return -1;
		}

		if (parent1 != null && parent2 == null) {
			return 1;
		}

		return parent1.getAbsolutePath().compareTo(parent2.getAbsolutePath());
	}

	/**
	 * @return Returns the comparesDirectory.
	 */
	public boolean isComparesDirectory() {
		return comparesDirectory;
	}

	/**
	 * @param comparesDirectory
	 *            The comparesDirectory to set.
	 */
	public void setComparesDirectory(boolean comparesDirectory) {
		this.comparesDirectory = comparesDirectory;
	}
	
	/**
	 * 比較を行うか判定する。
	 * 
	 * @return
	 */
	public boolean compares() {
		return comparators != null && comparators.length > 0; 
	}
}
