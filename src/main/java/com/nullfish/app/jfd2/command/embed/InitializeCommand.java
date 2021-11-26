/*
 * Created on 2005/02/07
 *
 */
package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ui.container2.NumberedJFD2;
import com.nullfish.lib.vfs.VFile;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class InitializeCommand extends Command {

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		VFile configDir = jfd.getJFDOwner().getConfigDirectory();
		for(int i=0; i< NumberedJFD2.getCount(); i++) {
			NumberedJFD2.getJfdAt(i).init(configDir);
		}
	}
}
