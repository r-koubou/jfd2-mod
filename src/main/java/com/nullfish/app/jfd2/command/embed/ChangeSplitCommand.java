package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 分割方向変更コマンド
 * JFDFrame使用時専用
 * 
 * @author shunji
 */
public class ChangeSplitCommand extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFDOwner owner = getJFD().getJFDOwner();
		
		if(owner instanceof JFDFrame) {
			((JFDFrame)owner).changeSplit();
		}
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
