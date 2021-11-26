/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.JFDModel;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * ヒストリ前後コマンド
 * 
 * @author shunji
 */
public class DirHistoryCommand extends Command {
	public static final String DIRECTION = "direction";
	public static final String NEXT = "next";
	public static final String PREV = "prev";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		JFDModel model = jfd.getModel();
		VFile currentDir = model.getCurrentDirectory();
		VFile dir;
		if(NEXT.equals(getParameter(DIRECTION))) {
			dir = model.getHistory().next();
		} else {
			dir = model.getHistory().prev();
		}
		
		dir = VFS.getInstance(jfd).getFile(dir.getFileName());
		
		if(dir == null || dir.equals(currentDir)) {
			return;
		}
		
		dir.clearFileAttribute();
		dir.clearPermission();
		model.setDirectoryAsynchIfNecessary(dir, dir.getParent(), jfd);
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
