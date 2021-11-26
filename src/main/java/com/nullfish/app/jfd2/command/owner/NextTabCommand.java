package com.nullfish.app.jfd2.command.owner;

import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 分割方向変更コマンド
 * JFDFrame使用時専用
 * 
 * @author shunji
 */
public class NextTabCommand extends OwnerCommand {
	public static final String DELTA = "delta";

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		int delta = ((Integer)getParameter(DELTA)).intValue();
		
		JFDOwner owner = getJFDOwner();
		
		if(!(owner instanceof JFDFrame)) {
			return;
		}
		
		JFDFrame frame = (JFDFrame)owner;
		frame.increasePosition(delta);
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
