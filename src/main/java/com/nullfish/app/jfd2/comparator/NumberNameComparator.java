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
public class NumberNameComparator implements FileComparator {
	private int order;
	
	public NumberNameComparator(boolean ascend) {
		order = ascend ? 1 : -1;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.nullfish.app.jfd2.comparator.FileComparator#compare(com.nullfish.lib.vfs.VFile,
	 *      com.nullfish.lib.vfs.VFile)
	 */
	public int compare(VFile file1, VFile file2) {
		long[] name1 = file1.getFileName().getLowerLongArrayName();
		long[] name2 = file2.getFileName().getLowerLongArrayName();

		for (int i = 0; i < name1.length && i < name2.length; i++) {
			if (name1[i] != name2[i]) {
				return (name1[i] > name2[i] ? 1 : -1) * order;
			}
		}
		
		return (name1.length - name2.length) * order;
	}
}
