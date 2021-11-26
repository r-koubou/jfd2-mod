package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.lib.vfs.exception.VFSException;

public class DummyCommand extends Command {

	public boolean closesUnusingFileSystem() {
		return false;
	}

	public void doExecute() throws VFSException {
	}

}
