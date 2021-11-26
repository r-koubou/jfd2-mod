/*
 * Created on 2004/05/31
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.JFDOwner;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * 次のJFDインスタンス選択コマンド
 * 
 * @author shunji
 */
public class NextJFDCommand extends Command {
	public static final String DELTA = "delta";
	
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		int delta = ((Integer)getParameter(DELTA)).intValue();
		
		NumberedJFD2 jfd = (NumberedJFD2)getJFD();
		int index = jfd.getIndexOfJfd();
		index += delta;
		
		int jfdCount = NumberedJFD2.getCount();
		while(index < 0) {
			index += jfdCount;
		}
		
		while(index >= NumberedJFD2.getCount()) {
			index -= jfdCount;
		}
		
		NumberedJFD2 nextJfd = NumberedJFD2.getJfdAt(index);
		
		JFDOwner owner = nextJfd.getJFDOwner();
		if(owner == null) {
			return;
		}

		owner.setActiveComponent(nextJfd);
	}

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}
