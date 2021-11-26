package com.nullfish.app.jfd2.command.embed;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.command.Command;
import com.nullfish.app.jfd2.config.Configuration;
import com.nullfish.app.jfd2.resource.JFDResource;
import com.nullfish.lib.vfs.exception.VFSException;

public class ChangeDotFileVisibilityCommand extends Command {

	public boolean closesUnusingFileSystem() {
		return false;
	}

	public void doExecute() throws VFSException {
		JFD jfd = getJFD();
		Configuration config = jfd.getLocalConfiguration();
		boolean value = !((Boolean) config.getParam("filters_dot_file", Boolean.FALSE)).booleanValue();
		config.setParam("filters_dot_file", Boolean.valueOf(value));
		jfd.getModel().setFiltersFile(value);
		jfd.setMessage(JFDResource.LABELS.getString(value ? "dot_file_invisible" : "dot_file_visible"), 3000);
		
		jfd.getCommandManager().execute("refresh");
	}

}
