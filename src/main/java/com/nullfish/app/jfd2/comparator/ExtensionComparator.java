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
public class ExtensionComparator implements FileComparator {
	private int order; 
	public ExtensionComparator(boolean ascend) {
		order = ascend ? 1 : -1;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.comparator.FileComparator#compare(com.nullfish.lib.vfs.VFile, com.nullfish.lib.vfs.VFile)
	 */
	public int compare(VFile file1, VFile file2) {
		String extension1 = file1.getFileName().getLowerExtension();
		String extension2 = file2.getFileName().getLowerExtension();
		
		return extension1.compareTo(extension2) * order;
	}
}
