/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.app.jfd2.comparator;

import com.nullfish.lib.vfs.FileType;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class FileTypeComparator implements FileComparator {

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.comparator.FileComparator#compare(com.nullfish.lib.vfs.VFile, com.nullfish.lib.vfs.VFile)
	 */
	public int compare(VFile file1, VFile file2) {
		try {
			FileType type1 = file1.getAttribute().getFileType();
			FileType type2 = file2.getAttribute().getFileType();
			if(type1 == type2) {
				return 0;
			}
			
			if(type1 == FileType.DIRECTORY) {
				return -1;
			} else {
				return 1;
			}
		} catch (VFSException e) {
			return 0;
		}
	}
}
