/*
 * Created on 2004/06/07
 *
 */
package com.nullfish.app.jfd2.comparator;

import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class TimestampComparator implements FileComparator {
	private int order;
	
	public TimestampComparator(boolean ascend) {
		order = ascend ? 1 : -1;
	}
	
	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.comparator.FileComparator#compare(com.nullfish.lib.vfs.VFile, com.nullfish.lib.vfs.VFile)
	 */
	public int compare(VFile file1, VFile file2) {
		try {
			long date1 = file1.getAttribute().getDate().getTime();
			long date2 = file2.getAttribute().getDate().getTime();
			if(date1 == date2) {
				return 0;
			}

			return (date2 > date1 ? -1 : 1) * order;
		} catch (VFSException e) {
			return 0;
		}
	}
}
