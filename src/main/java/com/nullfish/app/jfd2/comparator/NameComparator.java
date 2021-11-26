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
public class NameComparator implements FileComparator {
	private int order;
	
	public NameComparator(boolean ascend) {
		order = ascend ? 1 : -1;
	}
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.comparator.FileComparator#compare(com.nullfish.lib.vfs.VFile, com.nullfish.lib.vfs.VFile)
	 */
	public int compare(VFile file1, VFile file2) {
		if(file1.isRoot() && file2.isRoot()) {
			return file1.getSecurePath().toLowerCase().compareTo(file2.getSecurePath().toLowerCase());
		}
		return (file1.getFileName().getLowerName().compareTo(file2.getFileName().getLowerName())) * order;
	}
}
