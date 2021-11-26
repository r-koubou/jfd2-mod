package com.nullfish.app.jfd2.command.owner;

import javax.swing.JFrame;

import com.nullfish.app.jfd2.ui.container2.JFDFrame;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * フレーム切り替えコマンド
 * JFDFrame使用時専用
 * 
 * @author shunji
 */
public class NextFrameCommand extends OwnerCommand {
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
		int index = JFDFrame.getIndexOf(frame);
		JFDFrame nextFrame = JFDFrame.getInstance(index + delta);
		if(nextFrame.getState() == JFrame.ICONIFIED) {
			nextFrame.setState(JFrame.NORMAL);
		}
		nextFrame.toFront();
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
