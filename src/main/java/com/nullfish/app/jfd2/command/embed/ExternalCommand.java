package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.ext_command_panel.ExternalCommandManager;
import com.nullfish.lib.vfs.exception.VFSException;

public class ExternalCommand extends Command {
	public boolean closesUnusingFileSystem() {
		return false;
	}

	public void doExecute() throws VFSException {
		String key = (String) getParameter("key");
		if (key != null && key.length() == 1) {
			int code = key.charAt(0);
			int set = -1;
			int number = 0;
			if (code >= 'a' && code <= 'z') {
				set = 0;
				number = code - 'a';
			} else if (code >= 'A' && code <= 'Z') {
				set = 1;
				number = code - 'A';
			}
			if (set >= 0) {
				ExternalCommandManager.getInstance().execute(set, number,
						getJFD());
			}
		}
	}
}
