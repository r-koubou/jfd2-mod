package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.exception.VFSException;

public class IncrementalSearchCommand2 extends Command {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
		getJFD().setIncrementalSearchMode(true);
		getJFD().getIncrementalSearcher().setBuffer("");
		getJFD().setMessage("");
    }

	/* (non-Javadoc)
	 * @see com.nullfish.app.jfd2.command.Command#closesUnusingFileSystem()
	 */
	public boolean closesUnusingFileSystem() {
		return false;
	}
}

