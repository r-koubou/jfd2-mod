package com.nullfish.app.jfd2.command.groovy;

import groovy.lang.Binding;
import groovy.ui.Console;

import com.nullfish.app.jfd2.JFD;
import com.nullfish.app.jfd2.ext_command.window.ConsoleFrame;
import com.nullfish.lib.vfs.VFS;
import com.nullfish.lib.vfs.exception.VFSException;

/**
 * @author shunji
 *
 */
public class GroovyConsoleCommand extends AbstractGroovyCommand {
	/* (non-Javadoc)
	 * @see com.nullfish.lib.vfs.manipulation.abst.AbstractManipulation#doExecute()
	 */
	public void doExecute() throws VFSException {
			Binding binding = new Binding();
			binding.setVariable("jfd", getJFD());
			binding.setVariable("command", this);
			binding.setVariable("vfs", VFS.getInstance(getJFD()));
			binding.setVariable("console", ConsoleFrame.getInstance());
			Console console = new Console(binding);
			console.run();
	}
	
}
